package org.testpackage.test;

import org.junit.Before;
import org.junit.Test;
import org.testpackage.TestPackage;
import org.testpackage.TestPackageException;

import java.io.IOException;

import static org.testpackage.VisibleAssertions.assertEquals;
import static org.testpackage.VisibleAssertions.assertTrue;
import static org.testpackage.VisibleAssertions.fail;

/**
 * Created by richardnorth on 01/01/2014.
 */
public class OutputLevelTest extends StreamCaptureBaseTest {


    @Before
    public void setupStreamCapturing() throws IOException {
        super.setupStreamCapturing();
        System.setProperty("package", "org.testpackage.runnertest.outputleveltests");
    }

    @Test
    public void testQuietLevelBehaviour() throws IOException {

        TestPackage testPackage = new TestPackage();
        testPackage.quiet = true;
        testPackage.run();

        String capturedStdOut = getCapturedStdOut();

        assertTrue("Passing test method names are not shown in standard out", !capturedStdOut.contains("SimpleTest.passing"));
        assertTrue("Failing test method names are shown in standard out", capturedStdOut.contains("SimpleTest.failing"));

        assertTrue("Passing test method stdout is not shown in standard out", !capturedStdOut.contains("Stdout for passing test"));
        assertTrue("Failing test method stdout is not shown in standard out", !capturedStdOut.contains("Stdout for failing test"));

        assertTrue("Test summary is shown in standard out", capturedStdOut.contains("*** TESTS COMPLETE"));
        assertTrue("Test summary is shown in standard out", capturedStdOut.contains("*** 1 passed"));

        assertTrue("Failures listing is not shown in standard out", !capturedStdOut.contains("Failures:"));
        assertTrue("Failures listing is not shown in standard out", !capturedStdOut.contains("failing(org.testpackage.runnertest.outputleveltests.SimpleTest)"));

        assertTrue("Final test result is shown in standard out", capturedStdOut.contains("FAILED"));
    }

    @Test
    public void testVerboseLevelBehaviour() throws IOException {

        TestPackage testPackage = new TestPackage();
        testPackage.verbose = true;
        testPackage.run();

        String capturedStdOut = getCapturedStdOut();

        assertTrue("Passing test method names are shown in standard out", capturedStdOut.contains("SimpleTest.passing"));
        assertTrue("Failing test method names are shown in standard out", capturedStdOut.contains("SimpleTest.failing"));

        assertTrue("Passing test method stdout is shown in standard out", capturedStdOut.contains("Stdout for passing test"));
        assertTrue("Failing test method stdout is shown in standard out", capturedStdOut.contains("Stdout for failing test"));

        assertTrue("Test summary is shown in standard out", capturedStdOut.contains("*** TESTS COMPLETE"));
        assertTrue("Test summary is shown in standard out", capturedStdOut.contains("*** 1 passed"));

        assertTrue("Failures listing is shown in standard out", capturedStdOut.contains("Failures:"));
        assertTrue("Failures listing is shown in standard out", capturedStdOut.contains("failing(org.testpackage.runnertest.outputleveltests.SimpleTest)"));

        assertTrue("Final test result is shown in standard out", capturedStdOut.contains("FAILED"));
    }

    @Test
    public void testCorrectFailureWhenBothFlagsSet() throws IOException {

        TestPackage testPackage = new TestPackage();
        testPackage.verbose = true;
        testPackage.quiet = true;

        try {
            testPackage.run();
            fail("Exception should have been thrown but was not");
        } catch (TestPackageException ignored) {
            assertEquals("Error shows that quiet and verbose flags cannot be used together", "Quiet and Verbose flags cannot be used simultaneously", ignored.getMessage());
        }
    }

}
