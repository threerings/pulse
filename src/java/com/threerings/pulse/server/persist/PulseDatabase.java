package com.threerings.pulse.server.persist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import com.samskivert.depot.PersistenceContext;

/**
 * An annotation that identifies the {@link PersistenceContext} used by pulse.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@BindingAnnotation
public @interface PulseDatabase
{
}