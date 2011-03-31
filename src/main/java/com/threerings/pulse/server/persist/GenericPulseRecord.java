package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.Key;
import com.samskivert.depot.annotation.Column;
import com.samskivert.depot.annotation.Index;
import com.samskivert.depot.expression.ColumnExp;

public class GenericPulseRecord extends PulseRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<GenericPulseRecord> _R = GenericPulseRecord.class;
    public static final ColumnExp<String> CLAZZ = colexp(_R, "clazz");
    public static final ColumnExp<String> FIELD = colexp(_R, "field");
    public static final ColumnExp<Double> VALUE = colexp(_R, "value");
    public static final ColumnExp<Timestamp> RECORDED = colexp(_R, "recorded");
    public static final ColumnExp<String> SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** Increment this when making any schema changes. */
    public static final int SCHEMA_VERSION = 4;

    /** The name of the "class." */
    @Column(length=127) @Index(name="ixClazzField")
    public String clazz;

    /** The name of the "field." */
    @Column(length=127) @Index(name="ixClazzField")
    public String field;

    /** The stored value. */
    public double value;

}
