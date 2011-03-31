//
// $Id$

package com.threerings.pulse.server;

import com.threerings.pulse.server.persist.GenericPulseRecord;
import com.threerings.pulse.server.persist.PulseRecord;

/**
 * Collects generic information.
 */
public abstract class GenericPulseRecorder implements AbstractPulseManager.Recorder
{
    /**
     * Creates a new generic pulse recorder.
     */
    public GenericPulseRecorder (String clazz, String field)
    {
        _clazz = clazz;
        _field = field;
    }

    // from interface PulseManager.Recorder
    public Class<? extends PulseRecord> getRecordClass ()
    {
        return GenericPulseRecord.class;
    }

    // from interface PulseManager.Recorder
    public PulseRecord takePulse (long now)
    {
        GenericPulseRecord record = new GenericPulseRecord();
        record.clazz = _clazz;
        record.field = _field;
        record.value = getValue(now);
        return record;
    }

    /**
     * Returns the value to record in the current pulse.
     */
    protected abstract double getValue (long now);

    /** The "class" to store in the pulse record. */
    protected String _clazz;

    /** The "field" to store in the pulse record. */
    protected String _field;
}
