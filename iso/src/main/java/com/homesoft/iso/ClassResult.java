package com.homesoft.iso;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This method will be called when {@link BoxReader#read(Box, StreamReader, int)} returns
 * a {@link Class} instance that matches this method's parameter.
 * The convention for this would be set<i>SimpleClassName</i>(...)
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassResult {
    /**
     * Additional subclasses of this methods parameter to include
     */
    Class<?>[] value() default {};
}
