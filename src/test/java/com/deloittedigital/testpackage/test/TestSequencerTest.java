package com.deloittedigital.testpackage.test;

import com.deloittedigital.testpackage.TestSequencer;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.Request;

import java.io.IOException;
import java.util.Map;

import static com.deloittedigital.testpackage.VisibleAssertions.assertEquals;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class TestSequencerTest {

    @Test
    public void testSimpleContains() throws IOException {
        Request request = new TestSequencer().sequenceTests("com.deloittedigital.testpackage.runnertest.simpletests");

        assertEquals("the request contains the right number of test methods", request.getRunner().testCount(), 2);
        assertEquals("the request has the test methods in lexicographic order (1/2)", request.getRunner().getDescription().getChildren().get(0).getChildren().get(0).getDisplayName(), "testTrue1(com.deloittedigital.testpackage.runnertest.simpletests.SimpleTest)");
        assertEquals("the request has the test methods in lexicographic order (2/2)", request.getRunner().getDescription().getChildren().get(0).getChildren().get(1).getDisplayName(), "testTrue2(com.deloittedigital.testpackage.runnertest.simpletests.SimpleTest)");

    }

    @Test
    public void testRecentFailurePrioritisation() throws IOException {

        Map<String, Integer> runsSinceLastFailures = Maps.newHashMap();
        runsSinceLastFailures.put("com.deloittedigital.testpackage.runnertest.failureprioritisationtests.zzz_JustFailedTest", 0);
        runsSinceLastFailures.put("testTrue(com.deloittedigital.testpackage.runnertest.failureprioritisationtests.zzz_JustFailedTest)", 0);

        Request request = new TestSequencer().sequenceTests(runsSinceLastFailures, "com.deloittedigital.testpackage.runnertest.failureprioritisationtests");

        assertEquals("the request contains the right number of test methods", request.getRunner().testCount(), 3);
        assertEquals("the request has a just-failed test method first", request.getRunner().getDescription().getChildren().get(0).getChildren().get(0).getDisplayName(), "testTrue(com.deloittedigital.testpackage.runnertest.failureprioritisationtests.zzz_JustFailedTest)");
        assertEquals("the request has a never-failed test method which is a sibling of the just-failed method next", request.getRunner().getDescription().getChildren().get(0).getChildren().get(1).getDisplayName(), "testThatHasNotFailed(com.deloittedigital.testpackage.runnertest.failureprioritisationtests.zzz_JustFailedTest)");
        assertEquals("the request has a never-failed test method from a separate class last", request.getRunner().getDescription().getChildren().get(1).getChildren().get(0).getDisplayName(), "testTrue(com.deloittedigital.testpackage.runnertest.failureprioritisationtests.aaa_NoRecentFailuresTest)");

    }
}
