package org.testpackage.runnertest.outcomeaccounting;

import org.junit.Assume;
import org.junit.Test;

/**
 * Created by rnorth on 28/01/2017.
 */
public class TestWithAssumptionFailureTest {

    @Test
    public void test() {
        Assume.assumeTrue(false);
    }
}
