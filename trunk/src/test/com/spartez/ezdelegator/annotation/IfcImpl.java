package com.spartez.ezdelegator.annotation;

/**
 * User: kalamon
 * Date: 2009-05-06
 * Time: 23:03:40
 */
public class IfcImpl implements Ifc {
    public int t(int i) {
        return i + 1;
    }

    public int t2(int x, int y) {
        return x + y;
    }
}
