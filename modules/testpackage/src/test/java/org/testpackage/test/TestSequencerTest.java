package org.testpackage.test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.testpackage.TestSequencer;
import org.testpackage.runnertest.shardingtests.FirstTest;
import org.testpackage.runnertest.shardingtests.SecondTest;
import org.testpackage.runnertest.shardingtests.ThirdTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.testpackage.VisibleAssertions.assertEquals;
import static org.testpackage.VisibleAssertions.assertTrue;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class TestSequencerTest {

    private static final int TESTS_IN_SHARDING_PACKAGE = 6;
    private static final String SHARDING_TESTS_PACKAGE = "org.testpackage.runnertest.shardingtests";

    @Test
    public void testSimpleContains() throws IOException {
        Request request = new TestSequencer().sequenceTests("org.testpackage.runnertest.simpletests");

        assertEquals("the request contains the right number of test methods", 2, request.getRunner().testCount());
        assertEquals("the request has the test methods in lexicographic order (1/2)", "testTrue1(org.testpackage.runnertest.simpletests.SimpleTest)", request.getRunner().getDescription().getChildren().get(0).getChildren().get(0).getDisplayName());
        assertEquals("the request has the test methods in lexicographic order (2/2)", "testTrue2(org.testpackage.runnertest.simpletests.SimpleTest)", request.getRunner().getDescription().getChildren().get(0).getChildren().get(1).getDisplayName());

    }

    @Test
    public void testRecentFailurePrioritisation() throws IOException {

        Map<String, Integer> runsSinceLastFailures = Maps.newHashMap();
        runsSinceLastFailures.put("org.testpackage.runnertest.failureprioritisationtests.zzz_JustFailedTest", 0);
        runsSinceLastFailures.put("testTrue(org.testpackage.runnertest.failureprioritisationtests.zzz_JustFailedTest)", 0);

        Request request = new TestSequencer().sequenceTests(runsSinceLastFailures, "org.testpackage.runnertest.failureprioritisationtests");

        assertEquals("the request contains the right number of test methods", 3, request.getRunner().testCount());
        assertEquals("the first test method is one which has failed most recently", "testTrue(org.testpackage.runnertest.failureprioritisationtests.zzz_JustFailedTest)", request.getRunner().getDescription().getChildren().get(0).getChildren().get(0).getDisplayName());
        assertEquals("the second test method is one which hasn't ever failed but belongs to the same class as the first", "testThatHasNotFailed(org.testpackage.runnertest.failureprioritisationtests.zzz_JustFailedTest)", request.getRunner().getDescription().getChildren().get(0).getChildren().get(1).getDisplayName());
        assertEquals("the last test method is one which has never failed and belongs to class with no historical failures", "testTrue(org.testpackage.runnertest.failureprioritisationtests.aaa_NoRecentFailuresTest)", request.getRunner().getDescription().getChildren().get(1).getChildren().get(0).getDisplayName());

    }

    @Test
    public void testSharding() throws IOException {
        Request request;

        request = new TestSequencer(0, 1).sequenceTests(SHARDING_TESTS_PACKAGE);
        assertEquals("when there is only one shard, all the tests are run on it", TESTS_IN_SHARDING_PACKAGE, request.getRunner().testCount());

        request = new TestSequencer(0, 3).sequenceTests(SHARDING_TESTS_PACKAGE);
        assertEquals("when there are as many shards as test classes, the tests are evenly distributed", TESTS_IN_SHARDING_PACKAGE / 3, request.getRunner().testCount());

        Set<Class<?>> seenTestClasses = Sets.newHashSet();
        for (int i=0; i<3; i++) {
            request = new TestSequencer(i, 3).sequenceTests(SHARDING_TESTS_PACKAGE);

            final ArrayList<Description> testChildren = request.getRunner().getDescription().getChildren();
            final Class<?> testClass = testChildren.get(0).getTestClass();
            assertEquals("each shard has the right number of test classes", 1, testChildren.size());
            assertTrue("sharded tests are not duplicated", !seenTestClasses.contains(testClass));

            seenTestClasses.add(testClass);
        }

        assertTrue("all the tests are selected", seenTestClasses.contains(FirstTest.class) &&
                                                 seenTestClasses.contains(SecondTest.class) &&
                                                 seenTestClasses.contains(ThirdTest.class));

        Map<String, Integer> runsSinceLastFailures = Maps.newHashMap();
        runsSinceLastFailures.put( "testB(" + SHARDING_TESTS_PACKAGE + ".FirstTest)", 0);
        request = new TestSequencer(0, 3).sequenceTests(runsSinceLastFailures, SHARDING_TESTS_PACKAGE);

        assertEquals("sharding does not prevent tests from being prioritised", "testB(" + SHARDING_TESTS_PACKAGE + ".FirstTest)", request.getRunner().getDescription().getChildren().get(0).getChildren().get(0).getDisplayName());
        assertEquals("sharding does not prevent tests from being prioritised", "testA(" + SHARDING_TESTS_PACKAGE + ".FirstTest)", request.getRunner().getDescription().getChildren().get(0).getChildren().get(1).getDisplayName());

        request = new TestSequencer(7, 10).sequenceTests(SHARDING_TESTS_PACKAGE);
        assertTrue("when there are not enough tests, there is no error", !request.getRunner().getDescription().getChildren().toString().contains("initializationError"));
    }
}
