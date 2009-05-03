package com.spartez.ezdelegator;

/**
 * User: kalamon
 * Date: 2009-04-30
 * Time: 20:34:49
 */
public class TestImpl implements TestIfc {
    public int test(int arg) {
        return 0;
    }

    public int test2(int arg1, String arg2) {
        return 0;
    }

    public Object test3(String arg1, Object arg2, Long arg3, int arg4, boolean arg5) throws Exception {
        return null;
    }

    public void test4(long arg) {
    }

    public boolean test5() {
        return false;
    }

    public byte test6() {
        return 0;
    }

    public char test7() {
        return 'a';
    }

    public String test8() {
        return null;
    }

    public short test9(short arg) {
        return 0;
    }
}
