//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.expression.ColumnExp;

/**
 * Contains metrics for the Presents system.
 */
public class PresentsPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<PresentsPulseRecord> _R = PresentsPulseRecord.class;
    public static final ColumnExp<Integer> EVENT_COUNT = colexp(_R, "eventCount");
    public static final ColumnExp<Integer> MAX_EVENT_QUEUE_SIZE = colexp(_R, "maxEventQueueSize");
    public static final ColumnExp<Integer> UNIT_COUNT = colexp(_R, "unitCount");
    public static final ColumnExp<Integer> MAX_INVOKER_QUEUE_SIZE = colexp(_R, "maxInvokerQueueSize");
    public static final ColumnExp<Integer> CONNECTIONS = colexp(_R, "connections");
    public static final ColumnExp<Integer> CONNECTS = colexp(_R, "connects");
    public static final ColumnExp<Integer> DISCONNECTS = colexp(_R, "disconnects");
    public static final ColumnExp<Integer> CLOSES = colexp(_R, "closes");
    public static final ColumnExp<Integer> BYTES_IN = colexp(_R, "bytesIn");
    public static final ColumnExp<Integer> BYTES_OUT = colexp(_R, "bytesOut");
    public static final ColumnExp<Integer> MSGS_IN = colexp(_R, "msgsIn");
    public static final ColumnExp<Integer> MSGS_OUT = colexp(_R, "msgsOut");
    public static final ColumnExp<Timestamp> RECORDED = colexp(_R, "recorded");
    public static final ColumnExp<String> SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** Increment this when making any schema changes. */
    public static final int SCHEMA_VERSION = 1;

    /** The number of dobject events processed during the period. */
    public int eventCount;

    /** The largest size of the distributed object queue during the period. */
    public int maxEventQueueSize;

    /**
     * The number of invoker units processed during the period, each of which generally corresponds
     * to a set of database operations.
     */
    public int unitCount;

    /** The largest number of units queued to run during the period. */
    public int maxInvokerQueueSize;

    /** The number of dobj connections currently active. */
    public int connections;

    /** The number of new connections established in this period. */
    public int connects;

    /** The number of disconnections that happened in this period. */
    public int disconnects;

    /** The number of sockets cleanly closed in this period. */
    public int closes;

    /** The number of bytes read from our connections in this period. */
    public int bytesIn;

    /** The number of bytes written to our connections in this period. */
    public int bytesOut;

    /** The number of messages read from our connections in this period. */
    public int msgsIn;

    /** The number of messages written to our connections in this period. */
    public int msgsOut;

}
