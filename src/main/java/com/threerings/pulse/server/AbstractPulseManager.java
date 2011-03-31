//
// $Id: PulseManager.java 42 2009-08-24 17:16:18Z charlie $

package com.threerings.pulse.server;

import java.util.List;
import java.util.Set;

import java.sql.Timestamp;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.samskivert.jdbc.WriteOnlyUnit;

import com.threerings.pulse.server.persist.PulseRecord;
import com.threerings.pulse.server.persist.PulseRepository;

import static com.threerings.pulse.Log.log;

/**
 * Handles the collection and recording of data.
 */
public abstract class AbstractPulseManager
{
    /** Implemented by code that records pulses. */
    public interface Recorder
    {
        /**
         * Returns the type of record created by this recorder.
         */
        public Class<? extends PulseRecord> getRecordClass ();

        /**
         * Called periodically on the distributed object event thread to record a pulse. The
         * returned event will subsequently be written to the database. The record need not have
         * the {@link PulseRecord#recorded} nor {@link PulseRecord#server} fields filled in.
         *
         * @return the new pulse record, or <code>null</code> for none.
         */
        public PulseRecord takePulse (long now);
    }

    /** A recorder that generates multiple records per pulse. */
    public interface MultipleRecorder extends Recorder
    {
        /**
         * Records a pulse.
         *
         * @param results the list to which the pulse records should be added.
         */
        public void takePulse (long now, List<PulseRecord> results);
    }

    /** The frequency with which we record pulses. */
    public static final long PULSE_RECORD_FREQ = 3 * 60 * 1000L; // once per three minutes

    /** The frequency with which we prune old pulse data. */
    public static final long PULSE_PRUNE_FREQ = 60 * 60 * 1000L; // once an hour

    /**
     * Starts the pulse recording interval.
     *
     * @param server the identifier to use for this server.
     */
    public void init (String server)
    {
        _server = server;
        schedule(_pulseTaker, 500, PULSE_RECORD_FREQ);
    }

    protected abstract void schedule(Runnable toRun, long initialDelay, long period);

    protected abstract void invoke(WriteOnlyUnit unit);

    protected void takePulse ()
    {
        final List<PulseRecord> pulses = Lists.newArrayList();
        final long now = System.currentTimeMillis();

        for (Recorder recorder : _recorders) {
            try {
                PulseRecord pulse = recorder.takePulse(now);
                if (pulse != null) {
                    pulses.add(pulse);
                }
                if (recorder instanceof MultipleRecorder) {
                    ((MultipleRecorder)recorder).takePulse(now, pulses);
                }
            } catch (Exception e) {
                log.warning("Recorder failed", "recorder", recorder, e);
            }
        }
        invoke(new WriteOnlyUnit("takePulse") {
            @Override public void invokePersist () throws Exception {
                // store the pulses we've taken
                for (PulseRecord pulse : pulses) {
                    pulse.recorded = new Timestamp(now);
                    // Only set the server if there isn't a record-specific one
                    if (pulse.server == null) {
                        pulse.server = _server;
                    }
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

    protected final Runnable _pulseTaker = new Runnable() {
        public void run () {
            takePulse();
        }};

    protected String _server;
    protected long _lastPruneStamp = System.currentTimeMillis();

    @Inject protected Set<Recorder> _recorders;
    @Inject protected PulseRepository _pulseRepo;
}
