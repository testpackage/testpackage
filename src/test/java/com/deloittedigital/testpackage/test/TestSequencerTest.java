package com.deloittedigital.testpackage.test;

import com.deloittedigital.testpackage.TestSequencer;
import org.junit.Test;
import org.junit.runner.Request;

import java.io.IOException;

import static com.deloittedigital.testpackage.VisibleAssertions.assertEquals;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class TestSequencerTest {

    @Test
    public void testSimpleContains() throws IOException {
        Request request = new TestSequencer().sequenceTests("com.deloittedigital.testpackage.runnertest.simpletests");

        assertEquals("the request contains the right number of test methods", request.getRunner().testCount(), 1);
        assertEquals("the request has the test method by the right name", request.getRunner().getDescription().getChildren().get(0).getChildren().get(0).getDisplayName(), "testTrue(com.deloittedigital.testpackage.runnertest.simpletests.SimpleTest)");
    }
}
