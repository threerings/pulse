package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.expression.ColumnExp;

public class DepotPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<DepotPulseRecord> _R = DepotPulseRecord.class;
    public static final ColumnExp TOTAL_OPS = colexp(_R, "totalOps");
    public static final ColumnExp CONNECTION_WAIT_TIME = colexp(_R, "connectionWaitTime");
    public static final ColumnExp CACHED_QUERIES = colexp(_R, "cachedQueries");
    public static final ColumnExp UNCACHED_QUERIES = colexp(_R, "uncachedQueries");
    public static final ColumnExp EXPLICIT_QUERIES = colexp(_R, "explicitQueries");
    public static final ColumnExp CACHED_RECORDS = colexp(_R, "cachedRecords");
    public static final ColumnExp UNCACHED_RECORDS = colexp(_R, "uncachedRecords");
    public static final ColumnExp QUERY_TIME = colexp(_R, "queryTime");
    public static final ColumnExp MODIFIER_TIME = colexp(_R, "modifierTime");
    public static final ColumnExp OP_TIME_MEAN = colexp(_R, "opTimeMean");
    public static final ColumnExp RECORDED = colexp(_R, "recorded");
    public static final ColumnExp SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** Increment this when making any schema changes. */
    public static final int SCHEMA_VERSION = 2;

    /** The number of queries and modifiers executed. */
    public int ops;

    /** The total number of milliseconds spent waiting for a JDBC connection. */
    public long connectionWaitTime;

    /** The total number of collection queries that were loaded from the cache. */
    public int cachedQueries;

    /** The total number of collection queries that were loaded from the database. */
    public int uncachedQueries;

    /** The total number of one-phase collection queries that executed. */
    public int explicitQueries;

    /** The number of record loads (individual or as part of a collection query) that were
     * loaded from the cache. */
    public long cachedRecords;

    /** The number of record loads (individual or as part of a collection query) that were
     * loaded from the database. */
    public long uncachedRecords;

    /** The total number of milliseconds spent executing queries. */
    public long queryTime;

    /** The total number of milliseconds spent executing modifiers. */
    public long modifierTime;

    /** The mean time spent per op. */
    public double opTimeMean;

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link DepotPulseRecord}
     * with the supplied key values.
     */
    public static Key<DepotPulseRecord> getKey (Timestamp recorded, String server)
    {
        return new Key<DepotPulseRecord>(
                DepotPulseRecord.class,
                new ColumnExp[] { RECORDED, SERVER },
                new Comparable[] { recorded, server });
    }
    // AUTO-GENERATED: METHODS END
}
