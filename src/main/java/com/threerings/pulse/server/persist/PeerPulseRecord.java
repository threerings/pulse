//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.expression.ColumnExp;

/**
 * Contains metrics for the Presents Peer system.
 */
public class PeerPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<PeerPulseRecord> _R = PeerPulseRecord.class;
    public static final ColumnExp LOCKS_ACQUIRED = colexp(_R, "locksAcquired");
    public static final ColumnExp LOCK_ACQUIRE_WAIT = colexp(_R, "lockAcquireWait");
    public static final ColumnExp LOCKS_RELEASED = colexp(_R, "locksReleased");
    public static final ColumnExp LOCKS_HIJACKED = colexp(_R, "locksHijacked");
    public static final ColumnExp LOCK_TIMEOUTS = colexp(_R, "lockTimeouts");
    public static final ColumnExp NODE_ACTIONS_INVOKED = colexp(_R, "nodeActionsInvoked");
    public static final ColumnExp PEER_MESSAGES_IN = colexp(_R, "peerMessagesIn");
    public static final ColumnExp PEER_MESSAGES_OUT = colexp(_R, "peerMessagesOut");
    public static final ColumnExp RECORDED = colexp(_R, "recorded");
    public static final ColumnExp SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** Increment this when making any schema changes. */
    public static final int SCHEMA_VERSION = 1;

    /** The number of locks acquired during the period. */
    public int locksAcquired;

    /** The average time (in millis) spent waiting to acquire locks during the period. */
    public int lockAcquireWait;

    /** The number of locks released during the period. */
    public int locksReleased;

    /** The number of locks hijacked during the period. */
    public int locksHijacked;

    /** The number of lock requests that timed out during the period. */
    public int lockTimeouts;

    /** The number of node actions invoked during the period. */
    public int nodeActionsInvoked;

    /** The total number of messages received from all peers during the period. */
    public int peerMessagesIn;

    /** The total number of messages sent to all peers during the period. */
    public int peerMessagesOut;

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link PeerPulseRecord}
     * with the supplied key values.
     */
    public static Key<PeerPulseRecord> getKey (Timestamp recorded, String server)
    {
        return newKey(_R, recorded, server);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(RECORDED, SERVER); }
    // AUTO-GENERATED: METHODS END
}
