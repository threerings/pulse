//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.depot.DepotRepository;
import com.samskivert.depot.PersistenceContext;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.SchemaMigration;
import com.samskivert.depot.clause.FromOverride;
import com.samskivert.depot.clause.GroupBy;
import com.samskivert.depot.clause.OrderBy;
import com.samskivert.depot.clause.Where;
import com.samskivert.util.Calendars;

import com.threerings.pulse.server.AbstractPulseManager.Recorder;

/**
 * Provides for the writing and reading of pulse records.
 */
@Singleton
public class PulseRepository extends DepotRepository
{
    @Inject public PulseRepository (@PulseDatabase PersistenceContext ctx,
        Set<Recorder> recorders)
    {
        super(ctx);
        for (Recorder recorder : recorders) {
            addPulseRecord(recorder.getRecordClass());
        }

        ctx.registerMigration(GenericPulseRecord.class,
            new SchemaMigration.Retype(4, GenericPulseRecord.CLAZZ));
        ctx.registerMigration(GenericPulseRecord.class,
            new SchemaMigration.Retype(4, GenericPulseRecord.FIELD));
    }

    public void addPulseRecord (Class<? extends PulseRecord> record)
    {
        _records.add(record);
    }

    /**
     * Returns the set of records tracked by this repository.
     */
    public Set<Class<? extends PulseRecord>> getPulseRecords ()
    {
        return Collections.unmodifiableSet(_records);
    }

    /**
     * Retrieves and returns a multimap containing all generic "classes" as keys and each class's
     * "fields" as values.
     */
    public Multimap<String, String> loadGenericInfo ()
    {
        // find the unique class/field pairs
        List<ClassFieldRecord> records = findAll(ClassFieldRecord.class,
            new FromOverride(GenericPulseRecord.class),
            new GroupBy(GenericPulseRecord.CLAZZ, GenericPulseRecord.FIELD));

        // put them in a multimap
        Multimap<String, String> multimap = TreeMultimap.create();
        for (ClassFieldRecord record : records) {
            multimap.put(record.clazz, record.field);
        }
        return multimap;
    }

    /**
     * Loads generic records after the given timestamp;
     */
    public Collection<GenericPulseRecord> loadPulseHistory (
        String clazz, String field, Timestamp from)
    {
        return findAll(GenericPulseRecord.class,
            new Where(GenericPulseRecord.CLAZZ.eq(clazz).and(
                GenericPulseRecord.FIELD.eq(field).and(
                    GenericPulseRecord.RECORDED.greaterEq(from)))),
            OrderBy.ascending(GenericPulseRecord.RECORDED));
    }

    /**
     * Loads the data for the given type of record after the given timestamp.
     */
    public <T extends PulseRecord> Collection<T> loadPulseHistory (Class<T> type, Timestamp from)
    {
        return findAll(type, new Where(PulseRecord.RECORDED.as(type).greaterEq(from)),
            OrderBy.ascending(PulseRecord.RECORDED.as(type)));
    }

    /**
     * Records a pulse of data. The record must be fully initialized.
     */
    public void recordPulse (PulseRecord record)
    {
        insert(record);
    }

    /**
     * Prunes records older than seven days.
     */
    public void pruneData ()
    {
        // delete everything older than our prune cutoff (keep whole days)
        Timestamp cutoff = Calendars.now().zeroTime().addDays(-PRUNE_DAYS).toTimestamp();
        for (Class<? extends PersistentRecord> type : getPulseRecords()) {
            // no need to invalidate the cache, so pass null for the invalidator
            deleteAll(type, new Where(PulseRecord.RECORDED.as(type).lessThan(cutoff)));
        }
    }

    @Override // from DepotRepository
    protected void getManagedRecords (Set<Class<? extends PersistentRecord>> classes)
    {
        classes.addAll(_records);
    }

    /** The set of all pulse records managed by this repository. */
    protected Set<Class<? extends PulseRecord>> _records = Sets.newTreeSet(
        new Comparator<Class<?>>() {
        public int compare (Class<?> one, Class<?> two) {
            return one.getName().compareTo(two.getName());
        }
    });

    protected static final int PRUNE_DAYS = 7;
}
