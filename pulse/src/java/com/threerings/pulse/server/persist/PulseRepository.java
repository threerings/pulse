//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.samskivert.depot.DepotRepository;
import com.samskivert.depot.PersistenceContext;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.expression.ColumnExp;
import com.samskivert.depot.clause.OrderBy;
import com.samskivert.depot.clause.Where;
import com.samskivert.depot.operator.Conditionals.LessThan;
import com.samskivert.depot.operator.Conditionals.GreaterThanEquals;

/**
 * Provides for the writing and reading of pulse records.
 */
public class PulseRepository extends DepotRepository
{
    public PulseRepository (PersistenceContext ctx)
    {
        super(ctx);
    }

    /**
     * Returns the set of records tracked by this repository.
     */
    public Set<Class<? extends PersistentRecord>> getPulseRecords ()
    {
        Set<Class<? extends PersistentRecord>> classes = Sets.newHashSet();
        getManagedRecords(classes);
        return classes;
    }

    /**
     * Loads the data for the supplied set of records for the specified number of days. Supplying
     * zero will load the data for today, 1 the data for yesterday and today, etc. The records are
     * returned in ascending time order.
     */
    public <T extends PulseRecord> Collection<T> loadPulseHistory (Class<T> type, int days)
    {
        return findAll(type, new Where(new GreaterThanEquals(PulseRecord.RECORDED.as(type),
                                                             getTimestamp(days))),
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
        Timestamp cutoff = getTimestamp(PRUNE_DAYS);
        for (Class<? extends PersistentRecord> type : getPulseRecords()) {
            // no need to invalidate the cache, so pass null for the invalidator
            deleteAll(type, new Where(new LessThan(PulseRecord.RECORDED.as(type), cutoff)), null);
        }
    }

    protected Timestamp getTimestamp (int daysAgo)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -daysAgo);
        return new Timestamp(cal.getTimeInMillis());
    }

    // from DepotRepository
    protected void getManagedRecords (Set<Class<? extends PersistentRecord>> classes)
    {
        classes.add(JVMPulseRecord.class);
        classes.add(PresentsPulseRecord.class);
    }

    protected static final int PRUNE_DAYS = 7;
}
