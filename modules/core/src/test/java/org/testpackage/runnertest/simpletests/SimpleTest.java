package org.testpackage.runnertest.simpletests;

import org.junit.Test;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class SimpleTest {

    @Test
    public void testTrue2() throws InterruptedException {
        assert true; Thread.sleep(500L);
    }

    @Test
    public void testTrue1() throws InterruptedException {
        assert true; Thread.sleep(500L);
    }
}
