package com.deloittedigital.testpackage.test;

import com.deloittedigital.testpackage.TestPackage;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.deloittedigital.testpackage.VisibleAssertions.assertTrue;

/**
 * Created by richardnorth on 01/01/2014.
 */
public class FailFastTest extends StreamCaptureBaseTest {


    @Before
    public void setupStreamCapturing() throws IOException {
        super.setupStreamCapturing();
        System.setProperty("package", "com.deloittedigital.testpackage.runnertest.failfasttests");
    }

    @Test
    public void testAllTestsRunWithoutFlag() throws IOException {

        TestPackage testPackage = new TestPackage();
        testPackage.run();

        String capturedStdOut = getCapturedStdOut();
        assertTrue("stdout should contain '1 passed'", capturedStdOut.contains("1 passed"));
        assertTrue("stdout should contain '1 failed'", capturedStdOut.contains("1 failed"));
    }

    @Test
    public void testOnlyFailingTestRunWithFlag() throws IOException {

        TestPackage testPackage = new TestPackage();
        testPackage.failFast = true;
        testPackage.run();

        String capturedStdOut = getCapturedStdOut();
        assertTrue("stdout should NOT contain '1 passed'", !capturedStdOut.contains("1 passed"));
        assertTrue("stdout should contain 'TESTS ABORTED'", capturedStdOut.contains("TESTS ABORTED"));
    }
}
