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

package com.deloittedigital.testpackage;

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

    public static void assertEquals(String message, Object a, Object b) {
        if (a == null && b == null) {
            pass(message);
        } else if (a != null && a.equals(b)) {
            pass(message);
        } else {
            fail(message, "'" + a + "' does not equal '" + b + "'");
        }
    }

    private static void pass(String message) {
        ansiPrintf("        @|green " + TICK_MARK + " " + message + " |@");
    }

    private static void fail(String message, String hint) {
        ansiPrintf("        @|red " + CROSS_MARK + " " + message + " |@");

        if (hint != null) {
            ansiPrintf("            @|yellow " + hint + " |@");
        }

        throw new AssertionError(message);
    }

}
