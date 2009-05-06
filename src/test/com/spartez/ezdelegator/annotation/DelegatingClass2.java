package com.spartez.ezdelegator.annotation;

/**
 * User: kalamon
 * Date: 2009-05-07
 * Time: 00:21:30
 */
public abstract class DelegatingClass2 implements Ifc {
    
    @Delegate(ifc = Ifc.class)
    private IfcImpl impl = new IfcImpl();
}
