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

import static org.rnorth.visibleassertions.VisibleAssertions.assertEquals;
import static org.rnorth.visibleassertions.VisibleAssertions.assertTrue;

/**
 * Basic test class to demonstrate passing and failing tests.
 *
 * @author rnorth
 */
public class BasicTest {

    private static final long PAUSE = 1500L;

    @Test
    public void testBasicTrueAssertion() throws InterruptedException {
        assertTrue("it should be true", true);
        Thread.sleep(PAUSE);
    }

    @Test
    public void testBasicNullEqualsAssertion() throws InterruptedException {
        Thread.sleep(PAUSE);
        assertEquals("it should be equal", null, null);
    }

    @Test
    public void testEqualsAssertion() throws InterruptedException {
        Thread.sleep(PAUSE);
        assertEquals("it should be equal", "A", "A");
    }

    @Test
    public void testDeliberateError() throws InterruptedException {
        Thread.sleep(PAUSE);
        try {
            int a = 7 / 0;
        } catch (Throwable e) {
            throw new RuntimeException("Something went wrong! Oh noes!", e);
        }
    }

    @Test
    public void testDeliberateAssertionFailure() throws InterruptedException {
        Thread.sleep(PAUSE);
        assertEquals("Deliberate assertion failure - it should be equal but is not", 42, 99);
    }

    @Test
    public void testStreamCaptureExample() {
        System.out.println("A message on stdout");
        System.err.println("A message on stderr");
    }
}
