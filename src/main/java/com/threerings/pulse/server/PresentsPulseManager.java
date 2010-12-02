package com.threerings.pulse.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.samskivert.util.Invoker;

import com.samskivert.jdbc.WriteOnlyUnit;

import com.threerings.presents.annotation.MainInvoker;
import com.threerings.presents.server.PresentsDObjectMgr;

/**
 * Collects data on the distributed object event thread and writes to the database on the invoker
 * thread.
 */
@Singleton
public class PresentsPulseManager extends AbstractPulseManager
{
    @Override
    protected void schedule (Runnable toRun, long initialDelay, long period)
    {
        _omgr.newInterval(toRun).schedule(initialDelay, period, true);
    }

    @Override
    protected void invoke (WriteOnlyUnit unit)
    {
        _invoker.postUnit(unit);
    }

    @Inject protected PresentsDObjectMgr _omgr;

    @Inject protected @MainInvoker Invoker _invoker;
}
