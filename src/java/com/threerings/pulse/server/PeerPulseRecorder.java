//
// $Id$

package com.threerings.pulse.server;

import com.google.inject.Inject;

import com.threerings.presents.peer.server.PeerManager;

import com.threerings.pulse.server.persist.PeerPulseRecord;
import com.threerings.pulse.server.persist.PulseRecord;

/**
 * Collects information on the Presents Peer system.
 */
public class PeerPulseRecorder implements PulseManager.Recorder
{
    // from interface PulseManager.Recorder
    public Class<? extends PulseRecord> getRecordClass ()
    {
        return PeerPulseRecord.class;
    }

    // from interface PulseManager.Recorder
    public PulseRecord takePulse (long now)
    {
        PeerPulseRecord record = new PeerPulseRecord();

        PeerManager.Stats stats = _peerMan.getStats();
        record.locksAcquired = (int)(stats.locksAcquired - _last.locksAcquired);
        long periodWait = stats.lockAcquireWait - _last.lockAcquireWait;
        record.lockAcquireWait = (int)(periodWait / record.locksAcquired);
        record.locksReleased = (int)(stats.locksReleased - _last.locksReleased);
        record.locksHijacked = (int)(stats.locksHijacked - _last.locksHijacked);
        record.lockTimeouts = (int)(stats.lockTimeouts - _last.lockTimeouts);
        record.nodeActionsInvoked = (int)(stats.nodeActionsInvoked - _last.nodeActionsInvoked);
        record.peerMessagesIn = (int)(stats.peerMessagesIn.get() - _last.peerMessagesIn.get());
        record.peerMessagesOut = (int)(stats.peerMessagesOut - _last.peerMessagesOut);
        return record;
    }

    protected PeerManager.Stats _last = new PeerManager.Stats();

    @Inject protected PeerManager _peerMan;
}
