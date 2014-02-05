package org.testpackage.junitcore;

import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * Replacement for JUnitCore which provides access to its RunNotifier.
 * <p/>
 * Created by richardnorth on 01/01/2014.
 */
public class FailFastSupportCore {

    private final RunNotifier notifier = new RunNotifier();

    /**
     * Run all the tests contained in <code>request</code>.
     *
     * @param request the request describing tests
     * @return a {@link Result} describing the details of the test run and the failed tests.
     */
    public Result run(Request request) {
        return run(request.getRunner());
    }

    private Result run(Runner runner) {
        Result result = new Result();
        RunListener listener = result.createListener();
        notifier.addFirstListener(listener);
        try {
            notifier.fireTestRunStarted(runner.getDescription());
            runner.run(notifier);
            notifier.fireTestRunFinished(result);
        } finally {
            removeListener(listener);
        }
        return result;
    }

    /**
     * Add a listener to be notified as the tests run.
     *
     * @param listener the listener to add
     * @see org.junit.runner.notification.RunListener
     */
    public void addListener(RunListener listener) {
        notifier.addListener(listener);
    }

    /**
     * Remove a listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(RunListener listener) {
        notifier.removeListener(listener);
    }

    /**
     * Get the Run Notifier.
     *
     * @return the JUnit run notifier
     */
    public RunNotifier getNotifier() {
        return notifier;
    }
}
