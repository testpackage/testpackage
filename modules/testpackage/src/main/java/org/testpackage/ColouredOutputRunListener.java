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

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.twitter.common.testing.runner.StreamSource;
import jline.TerminalFactory;
import org.fusesource.jansi.Ansi;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.testpackage.streams.StreamCapture;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;
import static org.testpackage.AnsiSupport.ansiPrintf;

/**
 * A JUnit run listener which generates user-facing output on System.out to indicate progress of a test run.
 *
 * @author rnorth
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class ColouredOutputRunListener extends RunListener implements StreamSource {

    private static final String TICK_MARK = "\u2714";
    private static final String CROSS_MARK = "\u2718";

    private final boolean failFast;
    private final boolean verbose;
    private final boolean quiet;
    private final int testTotalCount;
    private final int terminalWidth;

    private StreamCapture streamCapture;
    private Description currentDescription;
    private long currentTestStartTime;
    private int testRunCount = 0;
    private int testFailureCount = 0;
    private int newTestFailureCount = 0;
    private int testIgnoredCount = 0;
    private boolean currentTestDidFail = false;

    private Map<Class, String> stdOutStreamStore = Maps.newHashMap();
    private Map<Class, String> stdErrStreamStore = Maps.newHashMap();

    public ColouredOutputRunListener(boolean failFast, boolean verbose, boolean quiet, int testTotalCount) {
        this.failFast = failFast;
        this.verbose = verbose;
        this.quiet = quiet;
        this.testTotalCount = testTotalCount;

        this.terminalWidth = TerminalFactory.get().getWidth();
    }

    @Override
    public void testStarted(Description description) throws Exception {
        if (!quiet) {
            displayTestMethodPlaceholder(description);
        }

        currentTestStartTime = System.currentTimeMillis();
        currentTestDidFail = false;

        // Tee output if not running in verbose mode, so that it is output in realtime
        boolean teeOutput = verbose && !quiet;
        streamCapture = StreamCapture.grabStreams(teeOutput, description.getDisplayName());
        currentDescription = description;
    }

    @Override
    public void testFailure(Failure failure) throws Exception {

        currentTestDidFail = true;
        testFailureCount++;

        StreamCapture.restore();

        replaceTestMethodPlaceholder(false);

        if (!quiet && !verbose && streamCapture.getStdOut().length() > 0) {
            System.out.println("    STDOUT:");
            System.out.print(streamCapture.getStdOut());
        }

        if (!quiet && !verbose && streamCapture.getStdErr().length() > 0) {
            System.out.println("\n    STDERR:");
            System.out.print(streamCapture.getStdErr());
        }


        if (failFast) {
            System.out.flush();
            System.out.println();
            System.out.println();
            System.out.println("*** TESTS ABORTED");
            ansiPrintf("*** @|bg_red Fail-fast triggered by test failure:|@\n");

            reportFailure(failure);
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {

        stdOutStreamStore.put(description.getTestClass(), streamCapture.getStdOut());
        stdErrStreamStore.put(description.getTestClass(), streamCapture.getStdErr());

        testRunCount++;

        if (!currentTestDidFail) {
            StreamCapture.restore();

            if (!quiet) {
                replaceTestMethodPlaceholder(true);
            }

            if (!quiet && !verbose && streamCapture.getStdOut().length() > 0) {
                System.out.println("    STDOUT:");
                System.out.print(streamCapture.getStdOut());
            }

            if (!quiet && !verbose && streamCapture.getStdErr().length() > 0) {
                System.out.println("\n    STDERR:");
                System.out.print(streamCapture.getStdErr());
            }
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        testIgnoredCount++;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void testRunFinished(Result result) throws Exception {

        int failureCount = result.getFailureCount();
        int testCount = result.getRunCount();
        int ignoredCount = result.getIgnoreCount();
        int passed = testCount - failureCount;
        List<Failure> failures = Lists.newArrayList();

        failures.addAll(result.getFailures());

        System.out.flush();
        System.out.println();
        System.out.println();
        System.out.println("*** TESTS COMPLETE");

        String passedStatement;
        if (passed > 0 && failureCount == 0) {
            passedStatement = "@|bg_green %d passed|@";
        } else {
            passedStatement = "%d passed";
        }
        String failedStatement;
        if (failureCount > 0) {
            failedStatement = "@|bg_red %d failed|@";
        } else {
            failedStatement = "0 failed";
        }
        String ignoredStatement;
        if (ignoredCount > 0 && ignoredCount > passed) {
            ignoredStatement = "@|bg_red %d ignored|@";
        } else if (ignoredCount > 0) {
            ignoredStatement = "@|bg_yellow %d ignored|@";
        } else {
            ignoredStatement = "0 ignored\n";
        }

        ansiPrintf("*** " + passedStatement + ", " + failedStatement + ", " + ignoredStatement, passed, failureCount, ignoredCount);

        if (failureCount > 0 && !quiet) {
            System.out.println();
            System.out.println();
            System.out.println("Failures:");
            for (Failure failure : failures) {
                reportFailure(failure);
            }
        }
        System.out.flush();
    }

    private void reportFailure(Failure failure) {
        ansiPrintf("    @|red %s|@:\n", failure.getDescription());
        ansiPrintf("      @|yellow %s: %s|@\n", failure.getException().getClass().getSimpleName(), indentNewlines(failure.getMessage()));
        Throwable exception = failure.getException();
        Throwable rootCause = Throwables.getRootCause(exception);

        if (exception.equals(rootCause)) {
            System.out.printf("        At %s\n\n", rootCause.getStackTrace()[0]);
        } else {
            System.out.printf("        At %s\n", exception.getStackTrace()[0]);
            ansiPrintf("      Root cause: @|yellow %s: %s|@\n", rootCause.getClass().getSimpleName(), indentNewlines(rootCause.getMessage()));
            System.out.printf("        At %s\n\n", rootCause.getStackTrace()[0]);
        }
        System.out.flush();
    }

    private static String indentNewlines(String textWithPossibleNewlines) {

        if (textWithPossibleNewlines == null) {
            return "";
        }

        return textWithPossibleNewlines.replaceAll("\\n", "\n      ");
    }

    private void displayTestMethodPlaceholder(Description description) {
        System.out.print(ansi().saveCursorPosition());
        final String thisTestDescription = ">>  " + description.getTestClass().getSimpleName() + "." + description.getMethodName();

        final StringBuffer overviewDescription = new StringBuffer();
        overviewDescription.append("[ ").append(testRunCount).append("/").append(testTotalCount).append(" tests run");
        if (testIgnoredCount > 0) {
            overviewDescription.append(", @|yellow ").append(testIgnoredCount).append(" ignored|@");
        }
        if (testFailureCount > 0) {
            overviewDescription.append(", @|red ").append(testFailureCount).append(" failed|@");
            if (newTestFailureCount > 0) {
                overviewDescription.append("@|bold,red  (0 new)|@");
            }
        }
        overviewDescription.append(" ] ");

        ansiPrintf(alignLeftRight(thisTestDescription, overviewDescription.toString()));
        if (verbose) {
            // Add newline so that tee-d stdout/err appear on the line below. In non-verbose mode we omit
            //  the newline so that this placeholder can be erased on completion of the test method.
            System.out.println();
        }
        System.out.flush();
    }

    private void replaceTestMethodPlaceholder(boolean success) {
        long elapsedTime = System.currentTimeMillis() - currentTestStartTime;
        System.out.print(ansi().eraseLine(Ansi.Erase.ALL).restorCursorPosition());
        String colour;
        String symbol;
        if (success) {
            colour = "green";
            symbol = TICK_MARK;
        } else {
            colour = "red";
            symbol = CROSS_MARK;
        }
        ansiPrintf(" @|" + colour + " %s  %s.%s|@ @|blue (%d ms)|@\n", symbol, currentDescription.getTestClass().getSimpleName(), currentDescription.getMethodName(), elapsedTime);
    }

    @Override
    public byte[] readOut(Class<?> testClass) throws IOException {
        if (this.stdOutStreamStore.get(testClass) != null) {
            return this.stdOutStreamStore.get(testClass).getBytes();
        } else {
            return new byte[0];
        }
    }

    @Override
    public byte[] readErr(Class<?> testClass) throws IOException {
        if (this.stdErrStreamStore.get(testClass) != null) {
            return this.stdErrStreamStore.get(testClass).getBytes();
        } else {
            return new byte[0];
        }
    }

    private String alignLeftRight(final String leftString, final String rightString) {

        String cleansedLeftString = leftString.replaceAll("@\\|[\\w,]+\\s|\\|@", "");
        String cleansedRightString = rightString.replaceAll("@\\|[\\w,]+\\s|\\|@", "");

        int space = terminalWidth - (cleansedLeftString.length() + cleansedRightString.length());

        return leftString + Strings.repeat(" ", space) + rightString;
    }
}
