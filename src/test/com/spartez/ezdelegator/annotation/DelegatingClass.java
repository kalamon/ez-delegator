package com.spartez.ezdelegator.annotation;

/**
 * User: kalamon
 * Date: 2009-05-06
 * Time: 23:04:21
 */
public abstract class DelegatingClass implements Ifc {

    @Delegate(ifc = Ifc.class)
    protected IfcImpl impl = new IfcImpl();
}
