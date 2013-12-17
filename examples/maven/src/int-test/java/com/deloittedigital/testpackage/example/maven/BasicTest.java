package com.deloittedigital.testpackage.example.maven;

import org.junit.Test;

import static com.deloittedigital.testpackage.VisibleAssertions.assertEquals;
import static com.deloittedigital.testpackage.VisibleAssertions.assertTrue;

/**
 * @author rnorth
 */
public class BasicTest {

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

    @Test
    public void testDeliberateFailure() {
        try {
            int a = 7 / 0;
        } catch (Throwable e) {
            throw new RuntimeException("Something went wrong! Oh noes!", e);
        }

    }
}
