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

    public static void displayContext(String contextDescription) {
        ansiPrintf("=== " + contextDescription + " ===");
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
