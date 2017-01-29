package org.testpackage.runnertest.outcomeaccounting;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by rnorth on 28/01/2017.
 */
public class TestWithFailingBeforeClassTest {

    @BeforeClass
    public static void setup() {
        throw new RuntimeException();
    }

    @Test
    public void test() {
        // do nothing
    }
}
