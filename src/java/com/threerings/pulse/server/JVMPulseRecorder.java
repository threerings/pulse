//
// $Id$

package com.threerings.pulse.server;

import com.threerings.pulse.server.persist.JVMPulseRecord;
import com.threerings.pulse.server.persist.PulseRecord;

/**
 * Records JVM stats.
 */
public class JVMPulseRecorder implements PulseManager.Recorder
{
    // from interface PulseManager.Recorder
    public Class<? extends PulseRecord> getRecordClass ()
    {
        return JVMPulseRecord.class;
    }

    // from interface PulseManager.Recorder
    public PulseRecord takePulse (long now)
    {
        JVMPulseRecord record = new JVMPulseRecord();
        Runtime rt = Runtime.getRuntime();
        record.usedHeap = (int)(rt.totalMemory()/MEGA);
        record.totalHeap = (int)((rt.totalMemory() - rt.freeMemory())/MEGA);
        record.maxHeap = (int)(rt.maxMemory()/MEGA);
        return record;
    }

    protected static final int MEGA = 1024*1024;
}
