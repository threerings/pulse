//
// $Id$

package com.threerings.pulse.server;

import java.sql.Timestamp;
import java.util.List;

import com.samskivert.jdbc.WriteOnlyUnit;
import com.samskivert.util.Interval;
import com.samskivert.util.Invoker;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import com.threerings.presents.annotation.MainInvoker;
import com.threerings.presents.server.PresentsDObjectMgr;
import com.threerings.presents.server.ShutdownManager;

import com.threerings.pulse.server.persist.PulseRecord;
import com.threerings.pulse.server.persist.PulseRepository;

import static com.threerings.pulse.Log.log;

/**
 * Handles the collection and recording of data. All data collection will take place on the
 * distributed object event thread. Writing of the data to the database takes place on the invoker
 * thread.
 */
@Singleton
public class PulseManager
    implements ShutdownManager.Shutdowner
{
    /** Implemented by code that records pulses. */
    public interface Recorder
    {
        /**
         * Called periodically on the distributed object event thread to record a pulse. The
         * returned event will subsequently be written to the database. The record need not have
         * the {@link PulseRecord#recorded} nor {@link PulseRecord#server} fields filled in.
         */
        public PulseRecord takePulse (long now);
    }

    /** The frequency with which we record pulses. */
    public static final long PULSE_RECORD_FREQ = 3 * 60 * 1000L; // once per three minutes

    /** The frequency with which we prune old pulse data. */
    public static final long PULSE_PRUNE_FREQ = 60 * 60 * 1000L; // once an hour

    @Inject public PulseManager (PresentsDObjectMgr omgr)
    {
        _pulser = new Interval(omgr) {
            public void expired () {
                takePulse();
            }
        };
    }

    /**
     * Starts the pulse recording interval.
     *
     * @param server the identifier to use for this server.
     */
    public void init (String server)
    {
        _server = server;
        _pulser.schedule(PULSE_RECORD_FREQ, true);
    }

    /**
     * Registers a recorder for pulse data. The recorder will immediately have its dependencies
     * injected into a newly created instance. All recorders are executed periodically to obtain
     * their data and turn it into a persistent record for storage.
     */
    public void registerRecorder (Class<? extends PulseRecord> record,
                                  Class<? extends Recorder> recorder)
    {
        _pulseRepo.addPulseRecord(record);
        _recorders.add(_injector.getInstance(recorder));
    }

    // from ShutdownManager.Shutdown
    public void shutdown ()
    {
        _pulser.cancel();
    }

    protected void takePulse ()
    {
        final List<PulseRecord> pulses = Lists.newArrayList();
        final long now = System.currentTimeMillis();

        for (Recorder recorder : _recorders) {
            try {
                pulses.add(recorder.takePulse(now));
            } catch (Exception e) {
                log.warning("Recorder failed", "recorder", recorder, e);
            }
        }

        _invoker.postUnit(new WriteOnlyUnit("takePulse") {
            public void invokePersist () throws Exception {
                // store the pulses we've taken
                for (PulseRecord pulse : pulses) {
                    pulse.recorded = new Timestamp(now);
                    pulse.server = _server;
                    _pulseRepo.recordPulse(pulse);
                }

                // prune old pulses if the time is right
                if (now - _lastPruneStamp > PULSE_PRUNE_FREQ) {
                    _lastPruneStamp = now;
                    _pulseRepo.pruneData();
                }
            }
        });
    }

    protected String _server;
    protected Interval _pulser;
    protected List<Recorder> _recorders = Lists.newArrayList();
    protected long _lastPruneStamp = System.currentTimeMillis();

    @Inject protected Injector _injector;
    @Inject protected @MainInvoker Invoker _invoker;
    @Inject protected PulseRepository _pulseRepo;
}
