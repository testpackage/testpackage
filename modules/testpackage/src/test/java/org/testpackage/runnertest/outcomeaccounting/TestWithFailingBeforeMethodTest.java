package org.testpackage.runnertest.outcomeaccounting;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by rnorth on 28/01/2017.
 */
public class TestWithFailingBeforeMethodTest {

    @Before
    public void setup() {
        throw new RuntimeException();
    }

    @Test
    public void test() {
        // do nothing
    }
}
