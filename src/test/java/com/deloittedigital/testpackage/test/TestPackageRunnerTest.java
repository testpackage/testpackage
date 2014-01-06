package com.deloittedigital.testpackage.test;

import com.deloittedigital.testpackage.TestPackage;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.deloittedigital.testpackage.VisibleAssertions.assertEquals;
import static com.deloittedigital.testpackage.VisibleAssertions.assertTrue;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class TestPackageRunnerTest extends StreamCaptureBaseTest {

    @Before
    public void setupStreamCapturing() throws IOException {
        super.setupStreamCapturing();
    }

    @Test
    public void testSimple() throws IOException {

        System.setProperty("package", "com.deloittedigital.testpackage.runnertest.simpletests");

        int exitCode = new TestPackage().run();

        String capturedStdOut = getCapturedStdOut();

        assertEquals("exit code should be zero for a successful run", 0, exitCode);
        assertTrue("stdout should contain the test name", capturedStdOut.contains(">> SimpleTest.testTrue"));
        assertTrue("stdout should contain 'TESTS COMPLETE'", capturedStdOut.contains("TESTS COMPLETE"));
        assertTrue("stdout should contain '2 passed'", capturedStdOut.contains("2 passed"));
        assertTrue("stdout should contain '0 failed'", capturedStdOut.contains("0 failed"));
        assertTrue("stdout should contain '0 ignored'", capturedStdOut.contains("0 ignored"));
        assertTrue("stdout should contain 'OK'", capturedStdOut.contains("OK"));
    }

    @Test
    public void testFailingTestPrioritisation() throws IOException {

        System.setProperty("package", "com.deloittedigital.testpackage.runnertest.failureprioritisationtests");

        new TestPackage().run();
        String capturedStdOut = getCapturedStdOut();
        int aaa_position = capturedStdOut.indexOf("aaa_NoRecentFailuresTest");
        int zzz_failedtestposition = capturedStdOut.indexOf("zzz_JustFailedTest");

        assertTrue("tests should run in lexicographic order if no history exists", aaa_position < zzz_failedtestposition);

        super.setupStreamCapturing();
        cleanup();
        Files.copy(new File("src/test/resources/historysample1.txt"), new File(".testpackage/history.txt"));
        new TestPackage().run();

        capturedStdOut = getCapturedStdOut();
        aaa_position = capturedStdOut.indexOf("aaa_NoRecentFailuresTest");
        zzz_failedtestposition = capturedStdOut.indexOf("zzz_JustFailedTest");

        assertTrue("tests should run to target recently failed tests", zzz_failedtestposition < aaa_position);
    }

    @Before
    @After
    public void cleanup() {
        new File(".testpackage/history.txt").delete();
    }
}
