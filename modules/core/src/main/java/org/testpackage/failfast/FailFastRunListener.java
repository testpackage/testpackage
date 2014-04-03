package org.testpackage.failfast;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * <p>
 * Run listener which will request that the test run be aborted if any failures occur.
 * </p>
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
