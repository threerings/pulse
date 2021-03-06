//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.expression.ColumnExp;

/**
 * Contains JVM metrics.
 */
public class JVMPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<JVMPulseRecord> _R = JVMPulseRecord.class;
    public static final ColumnExp<Integer> USED_HEAP = colexp(_R, "usedHeap");
    public static final ColumnExp<Integer> TOTAL_HEAP = colexp(_R, "totalHeap");
    public static final ColumnExp<Integer> MAX_HEAP = colexp(_R, "maxHeap");
    public static final ColumnExp<Timestamp> RECORDED = colexp(_R, "recorded");
    public static final ColumnExp<String> SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** Increment this when making any schema changes. */
    public static final int SCHEMA_VERSION = 1;

    /** The number of kilobytes of heap used. */
    public int usedHeap;

    /** The total size of the heap in kilobytes. */
    public int totalHeap;

    /** The maximum heap size in kilobytes.*/
    public int maxHeap;

}
