package com.spartez.ezdelegator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

/**
 * User: kalamon
 * Date: 2009-04-29
 * Time: 19:51:00
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Delegate {
    Class ifc();
}
