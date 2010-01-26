package com.threerings.pulse.server;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import com.threerings.pulse.server.AbstractPulseManager.Recorder;

/**
 * Binds classes to <code>Set&ltRecorder></code> when installed into an injector.
 */
public class PulseModule extends AbstractModule
{
    /**
     * Stores the given classes to bind when installed. Each class must implement {@link Recorder}.
     */
    public PulseModule (Class<?>... recorders)
    {
        for (Class<?> possibleRecorder : recorders) {
            Preconditions.checkArgument(Recorder.class.isAssignableFrom(possibleRecorder),
                "'" + possibleRecorder.getName() + "' does not implement Recorder");
        }
        @SuppressWarnings("unchecked")
        Class<? extends Recorder>[] asRecorder = (Class<? extends Recorder>[])recorders;
        _recorders = asRecorder;
    }

    @Override
    protected void configure ()
    {
        Multibinder<Recorder> recorders = Multibinder.newSetBinder(binder(), Recorder.class);
        for (Class<? extends Recorder> recorder : _recorders) {
            recorders.addBinding().to(recorder);
        }
    }

    protected final Class<? extends Recorder>[] _recorders;
}
