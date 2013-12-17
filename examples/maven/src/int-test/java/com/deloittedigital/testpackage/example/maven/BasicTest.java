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
