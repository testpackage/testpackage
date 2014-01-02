package com.deloittedigital.testpackage.runnertest.failureprioritisationtests;

import org.junit.Test;

/**
 * Named this way so as to come after aaa_NoRecentFailuresTest if in lexicographic order
 * <p/>
 * Created by richardnorth on 01/01/2014.
 */
public class zzz_JustFailedTest {

    @Test
    public void testTrue() {
        assert true;
    }

    @Test
    public void testThatHasNotFailed() {
        assert true;
    }
}
