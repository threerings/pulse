package com.threerings.pulse.web.server;

import static com.threerings.pulse.Log.log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import com.google.common.collect.Lists;

import com.threerings.pulse.server.persist.PulseRecord;

/** Provides generic access to a type of PulseRecord. */
public class RecordInfo
{
    public class FieldInfo {
        public final Field field;

        public FieldInfo (Field field) {
            this.field = field;
        }

        public String getName () {
            return field.getName();
        }

        public String getId () {
            return clazz.getSimpleName() + "." + getName();
        }

        public Number getValue (PulseRecord record) {
            try {
                return (Number)field.get(record);
            } catch (Exception e) {
                log.warning("Failed to fetch " + getId() + " from " + record, e);
                return 0;
            }
        }
    }

    public final Class<? extends PulseRecord> clazz;
    public final List<FieldInfo> fields = Lists.newArrayList();

    public RecordInfo (Class<? extends PulseRecord> clazz) {
        this.clazz = clazz;

        for (Field field : clazz.getFields()) {
            if (Modifier.isPublic(field.getModifiers()) &&
                !Modifier.isStatic(field.getModifiers()) &&
                !field.getDeclaringClass().equals(PulseRecord.class)) {
                fields.add(new FieldInfo(field));
            }
        }
    }

    public String getName () {
        return clazz.getSimpleName();
    }

    public FieldInfo getField (String name)
    {
        for (FieldInfo field : fields) {
            if(field.field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }
}