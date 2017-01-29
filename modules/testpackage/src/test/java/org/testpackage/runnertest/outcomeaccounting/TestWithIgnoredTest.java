package org.testpackage.runnertest.outcomeaccounting;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by rnorth on 28/01/2017.
 */
public class TestWithIgnoredTest {

    @Test @Ignore
    public void test() {
        // do nothing
    }

    @Test
    public void testNotIgnored() {
        // do nothing
    }
}
