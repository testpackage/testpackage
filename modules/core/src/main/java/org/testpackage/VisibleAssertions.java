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

package org.testpackage;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

/**
 * @author rnorth
 */
public class VisibleAssertions extends AnsiSupport {

    private static final String TICK_MARK = "\u2714";
    private static final String CROSS_MARK = "\u2718";

    // Utility class, hidden constructor
    protected VisibleAssertions() {

    }

    public static void assertTrue(String message, boolean value) {
        if (value) {
            pass(message);
        } else {
            fail(message, null);
        }
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        if (expected == null && actual == null) {
            pass(message);
        } else if (expected != null && expected.equals(actual)) {
            pass(message);
        } else {
            fail(message, "'" + actual + "' does not equal expected '" + expected + "'");
        }
    }

    public static void assertNull(String message, Object o) {
        if (o == null) {
            pass(message);
        } else {
            fail(message, "'" + o + "' is not null");
        }
    }

    public static void assertNotNull(String message, Object o) {
        if (o != null) {
            pass(message);
        } else {
            fail(message, null);
        }
    }

    public static void assertSame(String message, Object expected, Object actual) {
        if (expected == actual) {
            pass(message);
        } else {
            fail(message, "'" + actual + "' is not the same (!=) as expected '" + expected + "'");
        }
    }

    public static void fail(String message) {
        fail(message, null);
    }

    public static <T> void assertThat(String whatTheObjectIs, T actual, Matcher<? super T> matcher) {
        Description description = new StringDescription();
        if (matcher.matches(actual)) {
            description.appendText(whatTheObjectIs);
            description.appendText(" ");
            matcher.describeTo(description);
            pass(description.toString());
        } else {
            description.appendText("asserted that it ")
                    .appendDescriptionOf(matcher)
                    .appendText(" but ");
            matcher.describeMismatch(actual, description);
            fail("assertion on " + whatTheObjectIs + " failed", description.toString());
        }
    }

    private static void pass(String message) {
        initialize();
        ansiPrintf("        @|green " + TICK_MARK + " " + message + " |@\n");
    }

    private static void fail(String message, String hint) {
        initialize();
        ansiPrintf("        @|red " + CROSS_MARK + " " + message + " |@\n");

        if (hint == null) {
            throw new AssertionError(message);
        } else {
            ansiPrintf("            @|yellow " + hint + " |@\n");
            throw new AssertionError(message + ": " + hint);
        }

    }
}
