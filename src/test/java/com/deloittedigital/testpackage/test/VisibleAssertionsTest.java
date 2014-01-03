/*
 * Copyright 2013 Deloitte Digital and Richard North
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.deloittedigital.testpackage.test;

import org.junit.Test;

import static com.deloittedigital.testpackage.VisibleAssertions.*;

/**
 * @author rnorth
 */
public class VisibleAssertionsTest extends StreamCaptureBaseTest {

    @Test
    public void testTrueAssertion() {
        assertTrue("it should be true", true);
        assert getCapturedStdOut().contains("✔ it should be true");
    }

    @Test(expected = AssertionError.class)
    public void testFalseAssertion() {
        assertTrue("it should be true", false);
        assert getCapturedStdOut().contains("✘ it should be true");
    }

    @Test
    public void testNullEqualsAssertion() {
        assertEquals("it should be equal", null, null);
        assert getCapturedStdOut().contains("✔ it should be equal");
    }

    @Test
    public void testEqualsAssertion() {
        assertEquals("it should be equal", "A", "A");
        assert getCapturedStdOut().contains("✔ it should be equal");
    }

    @Test(expected = AssertionError.class)
    public void testOneNullEqualsAssertion() {
        assertEquals("it should be equal", "A", null);
        assert getCapturedStdOut().contains("✘ it should be equal");
        assert getCapturedStdOut().contains("'A' does not equal 'null'");
    }

    @Test(expected = AssertionError.class)
    public void testOneOtherNullEqualsAssertion() {
        assertEquals("it should be equal", null, "A");
        assert getCapturedStdOut().contains("✘ it should be equal");
        assert getCapturedStdOut().contains("'null' does not equal 'A'");
    }

    @Test(expected = AssertionError.class)
    public void testNotEqualsAssertion() {
        assertEquals("it should be equal", "A", "B");
        assert getCapturedStdOut().contains("✘ it should be equal");
        assert getCapturedStdOut().contains("'A' does not equal 'B'");
    }

    @Test(expected = RuntimeException.class)
    public void testDeliberateFailure() {
        try {
            int a = 7 /0;
        } catch (Throwable e) {
            throw new RuntimeException("A generic exception which wraps the root cause", e);
        }
    }

    @Test
    public void testNullAssertion() {
        assertNull("a null thing should be null", null);
        assert getCapturedStdOut().contains("✔ a null thing should be null");
    }

    @Test(expected = AssertionError.class)
    public void testFailingNullAssertion() {
        assertNull("a null thing should be null", "a non-null thing");
        assert getCapturedStdOut().contains("✘ a null thing should be null");
    }

    @Test
    public void testNotNullAssertion() {
        assertNotNull("a not-null thing should be not-null", "a non-null thing");
        assert getCapturedStdOut().contains("✔ a not-null thing should be not-null");
    }

    @Test(expected = AssertionError.class)
    public void testFailingNotNullAssertion() {
        assertNotNull("a not-null thing should be not-null", null);
        assert getCapturedStdOut().contains("✘ a not-null thing should be not-null");
    }

    @Test(expected = AssertionError.class)
    public void testFail() {
        fail("a failure reason");
        assert getCapturedStdOut().contains("✘ a failure reason");
    }

    @Test
    public void testSameAssertion() {
        Object o1 = "A";
        Object o2 = o1;
        assertSame("it should be the same", o1, o2);
        assert getCapturedStdOut().contains("✔ it should be the same");
    }

    @Test(expected = AssertionError.class)
    public void testFailingSameAssertion() {
        assertEquals("it should be the same", "A", "B");
        assert getCapturedStdOut().contains("✘ it should be the same");
        assert getCapturedStdOut().contains("'A' is not the same (!=) as 'B'");
    }
}
