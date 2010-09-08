//
// $Id: $

package com.threerings.pulse.jetty.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.thread.QueuedThreadPool;

import com.threerings.pulse.jetty.server.persist.JettyPulseRecord;
import com.threerings.util.RunningStats;

import com.threerings.pulse.server.AbstractPulseManager.Recorder;
import com.threerings.pulse.server.persist.PulseRecord;

import static com.threerings.pulse.Log.log;

/**
 * A Jetty server that adds a servlet resource counting pulse. To use it, wrap your servlets
 * in {@link JettyPulseServletHolder} instances before registering.
 */
public class JettyPulseHttpServer extends Server
    implements Recorder
{
    public JettyPulseHttpServer (int longResponse, int reallyLongResponse)
    {
        _longResponse = longResponse;
        _reallyLongResponse = reallyLongResponse;
    }
    
    public Class<? extends PulseRecord> getRecordClass ()
    {
        return JettyPulseRecord.class;
    }

    public PulseRecord takePulse (long now)
    {
        JettyPulseRecord rec = new JettyPulseRecord();
        RunningStats total, processing, queue;
        synchronized (_responseStatLock) {
            total = _responseTotal;
            processing = _responseProcessing;
            queue = _queuedRequests;
            _responseTotal = new RunningStats();
            _responseProcessing = new RunningStats();
            _queuedRequests = new RunningStats();
            rec.exceptions = _exceptions;
            _exceptions = 0;
        }
        rec.threads = getThreadPool().getThreads();
        if (queue.getNumSamples() > 0) {
            rec.queuedMax = queue.getMax();
            rec.queuedMean = queue.getMean();
        }
        rec.responseTotalMean = total.getMean();
        rec.responseTotalStdDev = total.getStandardDeviation();
        rec.responses = total.getNumSamples();
        rec.responseProcessingMean = processing.getMean();
        rec.responseProcessingStdDev = processing.getStandardDeviation();
        return rec;
    }

    /**
     * Counts resources used by held servlets.
     */
    protected class JettyPulseServletHolder extends ServletHolder
    {
        public JettyPulseServletHolder (HttpServlet servlet)
        {
            super(servlet);
        }

        @Override
        public void handle (ServletRequest req, ServletResponse resp)
            throws ServletException, IOException
        {
            long before = System.currentTimeMillis();
            IOTimingResponse timer = new IOTimingResponse((HttpServletResponse)resp);
            boolean exception = true;
            try {
                // TODO - wrap the request's io as well
                super.handle(req, timer);
                exception = false;
            } finally {
                if (exception) {
                    synchronized (_responseStatLock) {
                        _exceptions++;
                    }
                }
            }
            long totalDuration = System.currentTimeMillis() - before;
            long processingDuration = totalDuration - timer.getIODuration();

            if (processingDuration > _longResponse) {
                String msg = (processingDuration <= _reallyLongResponse) ?
                    "Long response generation" : "Really long response generation" ;                    
                log.warning(msg, "servlet", getName(),
                    "path", ((HttpServletRequest)req).getRequestURI(), "millis", processingDuration);
            }
            synchronized (_responseStatLock) {
                _queuedRequests.addSample(((QueuedThreadPool)getThreadPool()).getQueueSize());
                _responseTotal.addSample(totalDuration);
                _responseProcessing.addSample(processingDuration);
            }
        }
    }

    protected int _exceptions;
    protected RunningStats _responseTotal = new RunningStats();
    protected RunningStats _responseProcessing = new RunningStats();
    protected RunningStats _queuedRequests = new RunningStats();
    protected final Object _responseStatLock = new Object();

    final protected int _longResponse;
    final protected int _reallyLongResponse;
}
