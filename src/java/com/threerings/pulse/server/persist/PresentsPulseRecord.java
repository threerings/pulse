//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.expression.ColumnExp;

/**
 * Contains metrics for a Presents system.
 */
public class PresentsPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<PresentsPulseRecord> _R = PresentsPulseRecord.class;
    public static final ColumnExp EVENT_COUNT = colexp(_R, "eventCount");
    public static final ColumnExp MAX_EVENT_QUEUE_SIZE = colexp(_R, "maxEventQueueSize");
    public static final ColumnExp UNIT_COUNT = colexp(_R, "unitCount");
    public static final ColumnExp MAX_INVOKER_QUEUE_SIZE = colexp(_R, "maxInvokerQueueSize");
    public static final ColumnExp CONNECTIONS = colexp(_R, "connections");
    public static final ColumnExp BYTES_IN = colexp(_R, "bytesIn");
    public static final ColumnExp BYTES_OUT = colexp(_R, "bytesOut");
    public static final ColumnExp MSGS_IN = colexp(_R, "msgsIn");
    public static final ColumnExp MSGS_OUT = colexp(_R, "msgsOut");
    public static final ColumnExp RECORDED = colexp(_R, "recorded");
    public static final ColumnExp SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** Increment this when making any schema changes. */
    public static final int SCHEMA_VERSION = 1;

    /** The number of events processed during the period. */
    public int eventCount;

    /** The largest event queue size seen during the period. */
    public int maxEventQueueSize;

    /** The number of invoker units processed during the period. */
    public int unitCount;

    /** The largest invoker queue size seen during the period. */
    public int maxInvokerQueueSize;

    /** The number of dobj connections currently active. */
    public int connections;

    /** The number of bytes read from our dobj connections. */
    public int bytesIn;

    /** The number of bytes written to our dobj connections. */
    public int bytesOut;

    /** The number of messages read from our dobj connections. */
    public int msgsIn;

    /** The number of messages written to our dobj connections. */
    public int msgsOut;

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link PresentsPulseRecord}
     * with the supplied key values.
     */
    public static Key<PresentsPulseRecord> getKey (Timestamp recorded, String server)
    {
        return new Key<PresentsPulseRecord>(
                PresentsPulseRecord.class,
                new ColumnExp[] { RECORDED, SERVER },
                new Comparable[] { recorded, server });
    }
    // AUTO-GENERATED: METHODS END
}
