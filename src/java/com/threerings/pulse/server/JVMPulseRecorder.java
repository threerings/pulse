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
    public PulseRecord takePulse (long now)
    {
        JVMPulseRecord record = new JVMPulseRecord();
        Runtime rt = Runtime.getRuntime();
        record.usedHeap = (int)(rt.totalMemory()/1024);
        record.totalHeap = (int)(rt.totalMemory() - rt.freeMemory()/1024);
        record.maxHeap = (int)(rt.maxMemory()/1024);
        return record;
    }
}
