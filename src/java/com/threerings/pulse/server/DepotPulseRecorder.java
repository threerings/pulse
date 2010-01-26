package com.threerings.pulse.server;

import com.google.inject.Inject;

import com.samskivert.depot.PersistenceContext;
import com.samskivert.depot.Stats.Snapshot;

import com.threerings.pulse.server.PulseManager.Recorder;
import com.threerings.pulse.server.persist.DepotPulseRecord;
import com.threerings.pulse.server.persist.PulseRecord;

public class DepotPulseRecorder implements Recorder
{
    @Inject
    public DepotPulseRecorder (PersistenceContext ctx)
    {
        this(ctx, null);
    }

    public DepotPulseRecorder (PersistenceContext ctx, String server)
    {
        _ctx = ctx;
        _server = server;
    }

    public Class<? extends PulseRecord> getRecordClass ()
    {
        return DepotPulseRecord.class;
    }

    public PulseRecord takePulse (long now)
    {
        DepotPulseRecord record = new DepotPulseRecord();
        Snapshot shot = _ctx.getStats();

        // Fill in this record from the stats
        record.cachedQueries = shot.cachedQueries;
        record.cachedRecords = shot.cachedRecords;
        record.connectionWaitTime = shot.connectionWaitTime;
        record.explicitQueries = shot.explicitQueries;
        record.modifierTime = shot.modifierTime;
        record.queryTime = shot.queryTime;
        record.ops = shot.totalOps;
        record.uncachedQueries = shot.uncachedQueries;
        record.uncachedRecords = shot.uncachedRecords;

        // If we already had our pulse taken, subtract the old values out
        if (_previous != null) {
            record.cachedQueries -= _previous.cachedQueries;
            record.cachedRecords -= _previous.cachedRecords;
            record.connectionWaitTime -= _previous.connectionWaitTime;
            record.explicitQueries -= _previous.explicitQueries;
            record.modifierTime -= _previous.modifierTime;
            record.queryTime -= _previous.queryTime;
            record.ops -= _previous.totalOps;
            record.uncachedQueries -= _previous.uncachedQueries;
            record.uncachedRecords -= _previous.uncachedRecords;
        }
        record.opTimeMean = record.ops / (double)(record.modifierTime + record.queryTime);
        record.server = _server;

        return record;
    }

    protected Snapshot _previous;

    protected final String _server;

    protected final PersistenceContext _ctx;
}
