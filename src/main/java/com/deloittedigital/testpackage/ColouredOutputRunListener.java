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

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.List;

import static com.deloittedigital.testpackage.AnsiSupport.ansiPrintf;

/**
 *
 * @author rnorth
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class ColouredOutputRunListener extends RunListener {

    private final boolean failFast;

    public ColouredOutputRunListener(boolean failFast) {
        this.failFast = failFast;
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        if (failFast) {
            System.out.flush();
            System.out.println();
            System.out.println();
            System.out.println("*** TESTS ABORTED");
            ansiPrintf("*** @|bg_red Fail-fast triggered by test failure:|@");

            reportFailure(failure);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        System.out.println(">> " + description.getTestClass().getSimpleName() + "." + description.getMethodName() + ":");
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
            ignoredStatement = "0 ignored";
        }

        ansiPrintf("*** " + passedStatement + ", " + failedStatement + ", " + ignoredStatement, passed, failureCount, ignoredCount);

        if (failureCount > 0) {
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
        ansiPrintf("    @|yellow %s|@: @|bold,red %s: %s|@", failure.getDescription(), failure.getException().getClass().getSimpleName(), indentNewlines(failure.getMessage()));
        Throwable exception = failure.getException();
        Throwable rootCause = Throwables.getRootCause(exception);

        if (exception.equals(rootCause)) {
            System.out.printf("        At %s\n\n", rootCause.getStackTrace()[0]);
        } else {
            System.out.printf("        At %s\n", exception.getStackTrace()[0]);
            ansiPrintf("      Root cause: @|bold,red %s: %s|@", rootCause.getClass().getSimpleName(), indentNewlines(rootCause.getMessage()));
            System.out.printf("        At %s\n\n", rootCause.getStackTrace()[0]);
        }
        System.out.flush();
    }

    private static String indentNewlines(String textWithPossibleNewlines) {

        if (textWithPossibleNewlines == null) {
            textWithPossibleNewlines = "";
        }

        return textWithPossibleNewlines.replaceAll("\\n", "\n      ");
    }
}
