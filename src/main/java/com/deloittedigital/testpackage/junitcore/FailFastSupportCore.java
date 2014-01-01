package com.deloittedigital.testpackage.junitcore;

import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * Created by richardnorth on 01/01/2014.
 */
public class FailFastSupportCore {

    private final RunNotifier notifier = new RunNotifier();

    public Result run(Runner runner) {
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

    public void addListener(RunListener listener) {
        notifier.addListener(listener);
    }

    public void removeListener(RunListener listener) {
        notifier.removeListener(listener);
    }

    public RunNotifier getNotifier() {
        return notifier;
    }

    public Result run(Request request) {
        return run(request.getRunner());
    }
}
