package com.spartez.ezdelegator;

/**
 * User: kalamon
 * Date: 2009-04-30
 * Time: 20:35:17
 */
public class TestComposite implements TestIfc {
    private TestImpl testImpl = new TestImpl();

    public int test(int arg) {
        return testImpl.test(arg);
    }

    public int test2(int arg1, String arg2) {
        return testImpl.test2(arg1, arg2);
    }

    public Object test3(String arg1, Object arg2, Long arg3, int arg4, boolean arg5) throws Exception {
        return testImpl.test3(arg1, arg2, arg3, arg4, arg5);
    }

    public void test4(long arg) {
        testImpl.test4(arg);
    }

    public boolean test5() {
        return testImpl.test5();
    }

    public byte test6() {
        return testImpl.test6();
    }

    public char test7() {
        return testImpl.test7();
    }

    public String test8() {
        return testImpl.test8();
    }

    public short test9(short arg) {
        return testImpl.test9(arg);
    }
}
