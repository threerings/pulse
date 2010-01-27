package com.threerings.pulse.web.server;

import com.samskivert.util.LogBuilder;

/**
 * Indicates that a converter couldn't handle a parameter value in {@Parameters}.
 */
public class ConversionFailedException extends RuntimeException
{
    public ConversionFailedException (Throwable cause)
    {
        this(cause, "");
    }

    /**
     * Creates an exception message with the given base message and key value pairs as formatted
     * by {@link LogBuilder}.
     */
    public ConversionFailedException (Throwable cause, Object msg, Object...args)
    {
        this(msg, args);
        initCause(cause);
    }

    public ConversionFailedException (Object msg, Object...args)
    {
        _builder = new LogBuilder(msg, args);
    }

    public ConversionFailedException ()
    {
        this("");
    }

    /**
     * Adds the given key value pairs to the message.
     */
    public void append (Object... args)
    {
        _builder.append(args);
    }

    @Override
    public String getMessage ()
    {
        return _builder.toString();
    }

    protected final LogBuilder _builder;
}
