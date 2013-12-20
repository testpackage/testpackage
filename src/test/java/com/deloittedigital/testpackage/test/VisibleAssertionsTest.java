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

import static com.deloittedigital.testpackage.VisibleAssertions.assertEquals;
import static com.deloittedigital.testpackage.VisibleAssertions.assertTrue;

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
            throw new RuntimeException("Wrapping exception", e);
        }
    }
}
