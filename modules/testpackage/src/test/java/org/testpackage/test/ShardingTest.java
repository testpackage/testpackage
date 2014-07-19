package org.testpackage.test;

import org.junit.Before;
import org.junit.Test;
import org.testpackage.TestPackage;

import java.io.IOException;

import static org.testpackage.VisibleAssertions.assertEquals;
import static org.testpackage.VisibleAssertions.assertTrue;

/**
 * Created by richardnorth on 01/01/2014.
 */
public class ShardingTest extends StreamCaptureBaseTest {


    @Before
    public void setupPackageName() throws IOException {
        System.setProperty("package", "org.testpackage.runnertest.shardingtests");
    }

    @Test
    public void testAllTestsRunWithoutFlag() throws IOException {

        int testsFound = runShardedTest(1);
        assertEquals("all tests included when no sharding configured", 3, testsFound);

        testsFound = runShardedTest(3);
        assertEquals("tests are divided with sharding configured", 3, testsFound);
    }

    private int runShardedTest(int numShards) throws IOException {
        TestPackage testPackage = new TestPackage();
        testPackage.getConfiguration().setShardIndex(0);
        testPackage.getConfiguration().setNumberOfShards(numShards);
        testPackage.run();

        String capturedStdOut = getCapturedStdOut();
        int testsFound = 0;
        testsFound += capturedStdOut.contains("FirstTest") ? 1 : 0;
        testsFound += capturedStdOut.contains("SecondTest") ? 1 : 0;
        testsFound += capturedStdOut.contains("ThirdTest") ? 1 :0;

        if (numShards > 1) {
            assertTrue("test output includes message that sharding is in use", capturedStdOut.contains("Tests will be sharded"));
        }

        return testsFound;
    }

}
