//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.expression.ColumnExp;

/**
 * Contains JVM metrics.
 */
public class JVMPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<JVMPulseRecord> _R = JVMPulseRecord.class;
    public static final ColumnExp USED_HEAP = colexp(_R, "usedHeap");
    public static final ColumnExp TOTAL_HEAP = colexp(_R, "totalHeap");
    public static final ColumnExp MAX_HEAP = colexp(_R, "maxHeap");
    public static final ColumnExp RECORDED = colexp(_R, "recorded");
    public static final ColumnExp SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** Increment this when making any schema changes. */
    public static final int SCHEMA_VERSION = 1;

    /** The number of kilobytes of heap used. */
    public int usedHeap;

    /** The total size of the heap in kilobytes. */
    public int totalHeap;

    /** The maximum heap size in kilobytes.*/
    public int maxHeap;

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link JVMPulseRecord}
     * with the supplied key values.
     */
    public static Key<JVMPulseRecord> getKey (Timestamp recorded, String server)
    {
        return newKey(_R, recorded, server);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(RECORDED, SERVER); }
    // AUTO-GENERATED: METHODS END
}
