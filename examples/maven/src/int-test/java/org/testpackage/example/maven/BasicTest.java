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

package org.testpackage.example.maven;

import org.junit.Test;

import static org.testpackage.VisibleAssertions.assertEquals;
import static org.testpackage.VisibleAssertions.assertTrue;

/**
 * Basic test class to demonstrate passing and failing tests.
 *
 * @author rnorth
 */
public class BasicTest {

    @Test
    public void testBasicTrueAssertion() throws InterruptedException {
        assertTrue("it should be true", true);
        Thread.sleep(400L);
    }

    @Test
    public void testBasicNullEqualsAssertion() throws InterruptedException {
        Thread.sleep(400L);
        assertEquals("it should be equal", null, null);
    }

    @Test
    public void testEqualsAssertion() throws InterruptedException {
        Thread.sleep(400L);
        assertEquals("it should be equal", "A", "A");
    }

    @Test
    public void testDeliberateError() throws InterruptedException {
        Thread.sleep(400L);
        try {
            int a = 7 / 0;
        } catch (Throwable e) {
            throw new RuntimeException("Something went wrong! Oh noes!", e);
        }
    }

    @Test
    public void testDeliberateAssertionFailure() throws InterruptedException {
        Thread.sleep(400L);
        assertEquals("Deliberate assertion failure - it should be equal but is not", 42, 99);
    }

    @Test
    public void testStreamCaptureExample() {
        System.out.println("A message on stdout");
        System.err.println("A message on stderr");
    }
}
