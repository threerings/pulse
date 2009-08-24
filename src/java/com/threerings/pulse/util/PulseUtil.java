//
// $Id$

package com.threerings.pulse.util;

import java.util.Calendar;

import com.samskivert.util.CalendarUtil;

/**
 * Utility methods.
 */
public class PulseUtil
{
    /**
     * Returns the milliseconds at 00:00:00:000 on the specified number of days ago. Zero days ago
     * returns midnight of the current day, one days ago is midnight of the previous day, and so on
     * into the past.
     */
    public static long getStart (int daysAgo)
    {
        Calendar cal = CalendarUtil.zeroTime(Calendar.getInstance());
        cal.add(Calendar.DATE, -daysAgo);
        return cal.getTimeInMillis();
    }
}
