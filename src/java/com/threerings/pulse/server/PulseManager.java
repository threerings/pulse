package com.threerings.pulse.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.inject.Singleton;

import com.samskivert.jdbc.WriteOnlyUnit;

/**
 * Records pulses using its own thread.  When no longer in use, {@link #shutdown} must be called.
 */
@Singleton
public class PulseManager extends AbstractPulseManager
{
    @Override
    protected void schedule (Runnable toRun, long initialDelay, long delay)
    {
        _executor.scheduleWithFixedDelay(toRun, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void invoke (WriteOnlyUnit unit)
    {
        unit.invoke();
    }

    public void shutdown ()
    {
        _executor.shutdown();
    }

    protected ScheduledExecutorService _executor = Executors.newScheduledThreadPool(1,
        new ThreadFactory() {
        public Thread newThread (Runnable r) {
            return new Thread(r, "PulseTaker");
        }
    });

}
