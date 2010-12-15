package com.threerings.pulse.jetty.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.expression.ColumnExp;

import com.threerings.pulse.server.persist.PulseRecord;

public class JettyPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<JettyPulseRecord> _R = JettyPulseRecord.class;
    public static final ColumnExp<Integer> THREADS = colexp(_R, "threads");
    public static final ColumnExp<Double> RESPONSE_TOTAL_MEAN = colexp(_R, "responseTotalMean");
    public static final ColumnExp<Double> RESPONSE_TOTAL_STD_DEV = colexp(_R, "responseTotalStdDev");
    public static final ColumnExp<Double> RESPONSE_PROCESSING_MEAN = colexp(_R, "responseProcessingMean");
    public static final ColumnExp<Double> RESPONSE_PROCESSING_STD_DEV = colexp(_R, "responseProcessingStdDev");
    public static final ColumnExp<Integer> RESPONSES = colexp(_R, "responses");
    public static final ColumnExp<Integer> EXCEPTIONS = colexp(_R, "exceptions");
    public static final ColumnExp<Double> QUEUED_MAX = colexp(_R, "queuedMax");
    public static final ColumnExp<Double> QUEUED_MEAN = colexp(_R, "queuedMean");
    public static final ColumnExp<Timestamp> RECORDED = colexp(_R, "recorded");
    public static final ColumnExp<String> SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** Increment this when making any schema changes. */
    public static final int SCHEMA_VERSION = 3;

    /** The number of request threads active. */
    public int threads;

    /**
     * The average time spent in generating a response including IO in milliseconds during the
     * period.
     */
    public double responseTotalMean;

    /**
     * The standard deviation from the mean of time spent in generating a response including IO in
     * milliseconds during the period.
     */
    public double responseTotalStdDev;

    /**
     * The average time spent in app code for response generation in milliseconds during the
     * period.
     */
    public double responseProcessingMean;

    /**
     * The standard deviation from the mean of time spent in app code for response generation in
     * milliseconds during the period.
     */
    public double responseProcessingStdDev;

    /** The number responses generated during the period. */
    public int responses;

    /** Number of exceptions thrown by apps in response generation in the period. */
    public int exceptions;

    /** Maximum number of requests queued over this period. */
    public double queuedMax;

    /** Mean requests queued over this period. */
    public double queuedMean;

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link JettyPulseRecord}
     * with the supplied key values.
     */
    public static Key<JettyPulseRecord> getKey (Timestamp recorded, String server)
    {
        return newKey(_R, recorded, server);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(RECORDED, SERVER); }
    // AUTO-GENERATED: METHODS END
}
