package com.deloittedigital.testpackage;

import com.deloittedigital.testpackage.test.StreamCaptureBaseTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class TestPackageRunnerTest extends StreamCaptureBaseTest {

    @Before
    public void setup() throws IOException {
        super.setup();
        System.setProperty("package", "com.deloittedigital.testpackage.runnertest.simpletests");
    }

    @Test
    public void testSimple() throws IOException {
        int exitCode = new TestPackage().run();

        String capturedStdOut = getCapturedStdOut();

        VisibleAssertions.assertEquals("exit code should be zero for a successful run", exitCode, 0);
        VisibleAssertions.assertTrue("stdout should contain the test name", capturedStdOut.contains(">> SimpleTest.testTrue"));
        VisibleAssertions.assertTrue("stdout should contain 'TESTS COMPLETE'", capturedStdOut.contains("TESTS COMPLETE"));
        VisibleAssertions.assertTrue("stdout should contain '2 passed'", capturedStdOut.contains("2 passed"));
        VisibleAssertions.assertTrue("stdout should contain '0 failed'", capturedStdOut.contains("0 failed"));
        VisibleAssertions.assertTrue("stdout should contain '0 ignored'", capturedStdOut.contains("0 ignored"));
        VisibleAssertions.assertTrue("stdout should contain 'OK'", capturedStdOut.contains("OK"));
    }
}
