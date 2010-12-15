package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.expression.ColumnExp;

public class DepotPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<DepotPulseRecord> _R = DepotPulseRecord.class;
    public static final ColumnExp<Integer> OPS = colexp(_R, "ops");
    public static final ColumnExp<Long> CONNECTION_WAIT_TIME = colexp(_R, "connectionWaitTime");
    public static final ColumnExp<Integer> CACHED_QUERIES = colexp(_R, "cachedQueries");
    public static final ColumnExp<Integer> UNCACHED_QUERIES = colexp(_R, "uncachedQueries");
    public static final ColumnExp<Integer> EXPLICIT_QUERIES = colexp(_R, "explicitQueries");
    public static final ColumnExp<Long> CACHED_RECORDS = colexp(_R, "cachedRecords");
    public static final ColumnExp<Long> UNCACHED_RECORDS = colexp(_R, "uncachedRecords");
    public static final ColumnExp<Long> QUERY_TIME = colexp(_R, "queryTime");
    public static final ColumnExp<Long> MODIFIER_TIME = colexp(_R, "modifierTime");
    public static final ColumnExp<Double> OP_TIME_MEAN = colexp(_R, "opTimeMean");
    public static final ColumnExp<Timestamp> RECORDED = colexp(_R, "recorded");
    public static final ColumnExp<String> SERVER = colexp(_R, "server");
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
        return newKey(_R, recorded, server);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(RECORDED, SERVER); }
    // AUTO-GENERATED: METHODS END
}
