//
// $Id$

package com.threerings.pulse.server;

import java.util.List;

import com.threerings.pulse.server.persist.GenericPulseRecord;
import com.threerings.pulse.server.persist.PulseRecord;

/**
 * Collects generic information.
 */
public abstract class GenericPulseRecorder implements AbstractPulseManager.MultipleRecorder
{
    // from interface PulseManager.Recorder
    public Class<? extends PulseRecord> getRecordClass ()
    {
        return GenericPulseRecord.class;
    }

    // from interface PulseManager.Recorder
    public PulseRecord takePulse (long now)
    {
        return null;
    }

    // from interface PulseManager.MultipleRecorder
    public void takePulse (long now, List<PulseRecord> results)
    {
        _results = results;
        addValues(now);
        _results = null;
    }

    /**
     * Override to call {@link #add} for each value in the pulse.
     */
    protected abstract void addValues (long now);

    /**
     * Adds a value to the current list.
     */
    protected void add (String clazz, String field, double value)
    {
        GenericPulseRecord record = new GenericPulseRecord();
        record.clazz = clazz;
        record.field = field;
        record.value = value;
        _results.add(record);
    }

    /** The list to which we're adding, if any. */
    protected List<PulseRecord> _results;
}
