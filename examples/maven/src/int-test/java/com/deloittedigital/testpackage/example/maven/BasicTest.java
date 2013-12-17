package com.deloittedigital.testpackage.example.maven;

import org.junit.Test;

import static com.deloittedigital.testpackage.VisibleAssertions.assertEquals;
import static com.deloittedigital.testpackage.VisibleAssertions.assertTrue;

/**
 * Basic test class to demonstrate passing and failing tests.
 *
 * @author rnorth
 */
public class BasicTest {

    @Test
    public void testTrueAssertion() {
        assertTrue("it should be true", true);
    }

    @Test
    public void testFalseAssertion() {
        assertTrue("it should be false", false);
    }

    @Test
    public void testNullEqualsAssertion() {
        assertEquals("it should be equal", null, null);
    }

    @Test
    public void testEqualsAssertion() {
        assertEquals("it should be equal", "A", "A");
    }

    @Test
    public void testDeliberateError() {
        try {
            int a = 7 / 0;
        } catch (Throwable e) {
            throw new RuntimeException("Something went wrong! Oh noes!", e);
        }
    }

    @Test
    public void testDeliberateAssertionFailure() {
        assertEquals("Deliberate assertion failure - it should be equal but is not", 42, 99);
    }
}
