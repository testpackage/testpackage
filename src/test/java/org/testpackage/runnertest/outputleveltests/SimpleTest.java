package org.testpackage.runnertest.outputleveltests;

import org.junit.Test;

/**
 * @author rnorth
 */
public class SimpleTest {

    @Test
    public void passing() {
        System.out.println("Stdout for passing test");
        assert true;
    }

    @Test
    public void failing() {
        System.out.println("Stdout for failing test");
        assert false;
    }
}
