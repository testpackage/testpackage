package org.testpackage.test;

import org.junit.Before;
import org.junit.Test;
import org.testpackage.TestPackage;

import java.io.IOException;

import static org.rnorth.visibleassertions.VisibleAssertions.assertFalse;
import static org.rnorth.visibleassertions.VisibleAssertions.assertTrue;


/**
 * Created by rnorth on 28/01/2017.
 */
public class OutcomeAccountingTest extends StreamCaptureBaseTest {


    @Before
    public void setupPackageName() throws IOException {
        System.setProperty("package", "org.testpackage.runnertest.outcomeaccounting");
    }

    @Test
    public void test() throws IOException {

        TestPackage testPackage = new TestPackage();
        testPackage.getConfiguration().setQuiet(false);
        testPackage.run();

        String capturedStdOut = getCapturedStdOut();
        assertTrue("The failing setup method is indicated as the cause", capturedStdOut.contains("Suspect org.testpackage.runnertest.outcomeaccounting.TestWithFailingBeforeClassTest.setup"));
        assertTrue("The failing setup method is indicated as the cause", capturedStdOut.contains("Suspect org.testpackage.runnertest.outcomeaccounting.TestWithFailingBeforeMethodTest.setup"));
        assertFalse("There is no NullPointerException", capturedStdOut.contains("NullPointerException"));
        assertFalse("There is no EmptyStackException", capturedStdOut.contains("EmptyStackException"));


        assertTrue("The correct number of passes is shown", capturedStdOut.contains("1 passed"));
        assertTrue("The correct number of failures is shown", capturedStdOut.contains("2 failed"));
        assertTrue("The correct number of ignores is shown", capturedStdOut.contains("1 ignored"));
        assertTrue("The correct number of assumption failures is shown", capturedStdOut.contains("1 assumption(s) failed"));
    }

}