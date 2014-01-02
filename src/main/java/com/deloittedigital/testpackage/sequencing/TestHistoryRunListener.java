package com.deloittedigital.testpackage.sequencing;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Run listener which notifies a TestHistoryRepository of any failures when they occur.
 *
 * Created by richardnorth on 01/01/2014.
 */
public class TestHistoryRunListener extends RunListener {

    private final TestHistoryRepository testHistoryRepository;

    public TestHistoryRunListener(TestHistoryRepository testHistoryRepository) {
        this.testHistoryRepository = testHistoryRepository;
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        testHistoryRepository.markFailure(failure.getDescription().getClassName(), failure.getDescription().getDisplayName());
    }
}
