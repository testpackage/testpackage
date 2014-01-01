package com.deloittedigital.testpackage.failfast;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * Created by richardnorth on 01/01/2014.
 */
public class FailFastRunListener extends RunListener {

    private final RunNotifier notifier;

    public FailFastRunListener(RunNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        notifier.pleaseStop();
    }
}
