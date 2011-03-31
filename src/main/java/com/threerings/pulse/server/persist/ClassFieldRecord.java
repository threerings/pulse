package com.threerings.pulse.server.persist;

import com.samskivert.depot.Key;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.annotation.Column;
import com.samskivert.depot.annotation.Computed;
import com.samskivert.depot.annotation.Entity;
import com.samskivert.depot.expression.ColumnExp;

@Computed
@Entity
public class ClassFieldRecord extends PersistentRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<ClassFieldRecord> _R = ClassFieldRecord.class;
    public static final ColumnExp<String> CLAZZ = colexp(_R, "clazz");
    public static final ColumnExp<String> FIELD = colexp(_R, "field");
    // AUTO-GENERATED: FIELDS END

    /** The name of the "class." */
    @Computed(shadowOf=GenericPulseRecord.class)
    public String clazz;

    /** The name of the "field." */
    @Computed(shadowOf=GenericPulseRecord.class)
    public String field;
}
