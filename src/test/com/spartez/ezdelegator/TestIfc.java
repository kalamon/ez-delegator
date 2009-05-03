package com.spartez.ezdelegator;

/**
 * User: kalamon
 * Date: 2009-04-30
 * Time: 20:34:29
 */
public interface TestIfc {
    int test(int arg);
    int test2(int arg1, String arg2);
    Object test3(String arg1, Object arg2, Long arg3, int arg4, boolean arg5) throws Exception;
    void test4(long arg);
    boolean test5();
    byte test6();
    char test7();
    String test8();
    short test9(short arg);
}
