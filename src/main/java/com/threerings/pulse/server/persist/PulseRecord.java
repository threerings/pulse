//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.annotation.Index;
import com.samskivert.depot.expression.ColumnExp;

/**
 * A base class for all pulse data records.
 */
public abstract class PulseRecord extends PersistentRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<PulseRecord> _R = PulseRecord.class;
    public static final ColumnExp<Timestamp> RECORDED = colexp(_R, "recorded");
    public static final ColumnExp<String> SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** The time at which this data was sampled and recorded. Will be filled in by PulseManager. */
    @Index
    public Timestamp recorded;

    /**
     * The server that sampled and recorded the data. If not set when given to PulseManager, will
     * be filled in with the server value on the manager.
     */
    public String server;
}
