//
// $Id$

package com.threerings.pulse.server.persist;

import java.sql.Timestamp;

import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.expression.ColumnExp;
import com.samskivert.depot.annotation.Id;

/**
 * A base class for all pulse data records.
 */
public abstract class PulseRecord extends PersistentRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<PulseRecord> _R = PulseRecord.class;
    public static final ColumnExp RECORDED = colexp(_R, "recorded");
    public static final ColumnExp SERVER = colexp(_R, "server");
    // AUTO-GENERATED: FIELDS END

    /** The time at which this data was sampled and recorded. */
    @Id public Timestamp recorded;

    /** The server that sampled and recorded the data. */
    @Id public String server;
}
