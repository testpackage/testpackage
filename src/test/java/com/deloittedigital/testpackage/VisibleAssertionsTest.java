package com.deloittedigital.testpackage;

import org.junit.Ignore;
import org.junit.Test;

import static com.deloittedigital.testpackage.VisibleAssertions.assertEquals;
import static com.deloittedigital.testpackage.VisibleAssertions.assertTrue;

/**
 * @author rnorth
 */
public class VisibleAssertionsTest {

    @Test
    public void testTrueAssertion() {
        assertTrue("it should be true", true);
    }

    @Test(expected = AssertionError.class)
    public void testFalseAssertion() {
        assertTrue("it should be true", false);
    }

    @Test
    public void testNullEqualsAssertion() {
        assertEquals("it should be equal", null, null);
    }

    @Test
    public void testEqualsAssertion() {
        assertEquals("it should be equal", "A", "A");
    }

    @Test(expected = AssertionError.class)
    public void testOneNullEqualsAssertion() {
        assertEquals("it should be equal", "A", null);
    }

    @Test(expected = AssertionError.class)
    public void testOneOtherNullEqualsAssertion() {
        assertEquals("it should be equal", null, "A");
    }

    @Test(expected = AssertionError.class)
    public void testNotEqualsAssertion() {
        assertEquals("it should be equal", "A", "B");
    }

    @Test @Ignore
    public void testDeliberateFailure() {
        //assertEquals("this is a deliberate failure", "a", "b");
        try {
            int a = 7 /0;
        } catch (Throwable e) {
            throw new RuntimeException("Wrapping exception", e);
        }

    }
}
