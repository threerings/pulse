//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.depot.DepotRepository;
import com.samskivert.depot.PersistenceContext;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.clause.OrderBy;
import com.samskivert.depot.clause.Where;
import com.samskivert.depot.operator.GreaterThanEquals;
import com.samskivert.depot.operator.LessThan;

import com.threerings.pulse.util.PulseUtil;

/**
 * Provides for the writing and reading of pulse records.
 */
@Singleton
public class PulseRepository extends DepotRepository
{
    @Inject public PulseRepository (@PulseDatabase PersistenceContext ctx)
    {
        super(ctx);
    }

    /**
     * Returns the set of records tracked by this repository.
     */
    public Set<Class<? extends PulseRecord>> getPulseRecords ()
    {
        return Collections.unmodifiableSet(_records);
    }

    /**
     * Notes that the supplied record will be managed by this repository. This must be called
     * before the repository is initialized to avoid lazy initialization warnings.
     */
    public void addPulseRecord (Class<? extends PulseRecord> record)
    {
        _records.add(record);
    }

    /**
     * Loads the data for the supplied set of records for the specified number of days. One day is
     * the last 24 hours, two days, the last 48, etc.
     */
    public <T extends PulseRecord> Collection<T> loadPulseHistory (Class<T> type, int days)
    {
        Timestamp time = new Timestamp(System.currentTimeMillis() - days*24*60*60*1000L);
        return findAll(type, new Where(new GreaterThanEquals(PulseRecord.RECORDED.as(type), time)),
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
        Timestamp cutoff = new Timestamp(PulseUtil.getStart(PRUNE_DAYS));
        for (Class<? extends PersistentRecord> type : getPulseRecords()) {
            // no need to invalidate the cache, so pass null for the invalidator
            deleteAll(type, new Where(new LessThan(PulseRecord.RECORDED.as(type), cutoff)), null);
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
