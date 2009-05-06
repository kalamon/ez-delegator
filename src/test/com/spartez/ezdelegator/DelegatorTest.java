package com.spartez.ezdelegator;

import junit.framework.TestCase;
import com.spartez.ezdelegator.annotation.DelegatingClass;

/**
 * User: kalamon
 * Date: 2009-04-29
 * Time: 21:28:20
 */
public class DelegatorTest extends TestCase {

    public void testCreateSimpleObject() throws Exception {
        String s = Delegator.getInstance().createObject(String.class, "aaa");
        assertEquals(s, "aaa");
    }

    public void testCreateObjectNoArgs() throws Exception {
        String s = Delegator.getInstance().createObject(String.class);
        assertEquals(s, "");
    }

    public static class TestClass1 {
        public TestClass1(String s, int i) {
        }
    }

    public static class TestClass2 {
        public TestClass2(long l) {
        }
    }

    public static class TestClass3 {
        public TestClass3() {
        }
    }

    public static class TestClass4 {
        private String s;
        private int i;
        private long l;

        public TestClass4(String s, int i) {
            this.s = s;
            this.i = i;
        }

        public TestClass4(long l) {
            this.l = l;
        }

        public String rev(String txt) {
            return txt.toUpperCase();
        }
    }

    public void testCreateTestClassObject() throws Exception {
        Object tc = Delegator.getInstance().createObject(TestClass1.class, "aaa", 1);
        assertNotNull(tc);
    }

    public void testAttemptCreateFromBadArgs() throws Exception {
        try {
            Delegator.getInstance().createObject(TestClass1.class, "aaa", "bbb");
            fail("Attempt to create an instance from bad args list should fail");
        } catch (Delegator.DelegatorException e) {
            assertEquals("com.spartez.ezdelegator.Delegator$DelegatorException: No matching constructor found", e.getMessage());
        }
    }

    public void testCreateObjectOfTheSameClassTwice() throws Exception {
        Delegator.getInstance().createObject(TestClass1.class, "aaa", 1);
        Delegator.getInstance().createObject(TestClass1.class, "aaa", 1);
        assertEquals(1, Delegator.getInstance().getGeneratedClassesCount());
    }

    // this test fails without (long) cast because I don't know how
    // to cast Integer to Long automatically in the Delegator. Help!
    public void testCreateObjectWithNumberCast() throws Exception {
        Object tc = Delegator.getInstance().createObject(TestClass2.class, (long) 1);
        assertNotNull(tc);
    }

    public void testCreateTestClassEmptyCtorObject() throws Exception {
        Object tc = Delegator.getInstance().createObject(TestClass3.class);
        assertNotNull(tc);
    }

    public void testCreateObjectWithDelegateAnnotation() throws Exception {
        Object tc = Delegator.getInstance().createObject(TestComposite2.class);
        assertNotNull(tc);
    }

    public void testCreateTestClassObjectAndCastToOriginalClass() throws Exception {
        TestClass1 tc = Delegator.getInstance().createObject(TestClass1.class, "aaa", 1);
        assertNotNull(tc);
    }

    public void testCreateTestClassObjectsWithTwoConstructors() throws Exception {
        TestClass4 tc1 = Delegator.getInstance().createObject(TestClass4.class, "aaa", 1);
        assertNotNull(tc1);
        assertEquals(tc1.s,  "aaa");
        assertEquals(tc1.i,  1);
        TestClass4 tc2 = Delegator.getInstance().createObject(TestClass4.class, 2l);
        assertNotNull(tc2);
        assertEquals(tc2.l, 2l);
        String result = tc2.rev("abc");
        assertEquals("ABC", result);
    }

    public void testSimpleAnnotationWithProtectedField() throws Exception {
        DelegatingClass dc = Delegator.getInstance().createObject(DelegatingClass.class);
        assertNotNull(dc);
        int res1 = dc.t(1);
        assertEquals(2, res1);
        int res2 = dc.t2(2, 3);
        assertEquals(5, res2);
    }
}
