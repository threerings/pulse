//
// $Id$

package com.threerings.pulse.server;

import com.google.inject.Inject;

import com.threerings.presents.data.ConMgrStats;
import com.threerings.presents.server.PresentsDObjectMgr;
import com.threerings.presents.server.PresentsInvoker;
import com.threerings.presents.server.net.ConnectionManager;

import com.threerings.pulse.server.persist.PulseRecord;
import com.threerings.pulse.server.persist.PresentsPulseRecord;

/**
 * Obtains and records various basic Presents statistics.
 */
public class PresentsPulseRecorder implements AbstractPulseManager.Recorder
{
    // from interface PulseManager.Recorder
    public Class<? extends PulseRecord> getRecordClass ()
    {
        return PresentsPulseRecord.class;
    }

    // from interface PulseManager.Recorder
    public PulseRecord takePulse (long now)
    {
        PresentsPulseRecord record = new PresentsPulseRecord();

        PresentsDObjectMgr.Stats ostats = _omgr.getStats(true);
        record.eventCount = ostats.eventCount;
        record.maxEventQueueSize = ostats.maxQueueSize;

        PresentsInvoker.Stats istats = _invoker.getStats(true);
        record.unitCount = istats.unitsRun;
        record.maxInvokerQueueSize = istats.maxQueueSize;

        ConMgrStats cstats = _conmgr.getStats();
        record.connections = cstats.connectionCount;
        record.connects = cstats.connects - _lcstats.connects;
        record.disconnects = cstats.disconnects - _lcstats.disconnects;
        record.closes = cstats.closes - _lcstats.closes;
        record.bytesIn = (int)(cstats.bytesIn - _lcstats.bytesIn);
        record.bytesOut = (int)(cstats.bytesOut - _lcstats.bytesOut);
        record.msgsIn = (int)(cstats.msgsIn - _lcstats.msgsIn);
        record.msgsOut = (int)(cstats.msgsOut - _lcstats.msgsOut);
        _lcstats = cstats;

        return record;
    }

    protected ConMgrStats _lcstats = new ConMgrStats();

    @Inject protected PresentsDObjectMgr _omgr;
    @Inject protected PresentsInvoker _invoker;
    @Inject protected ConnectionManager _conmgr;
}
