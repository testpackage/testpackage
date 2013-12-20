package com.deloittedigital.testpackage;

import org.junit.Test;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class StreamCaptureSystemTest extends StreamCaptureBaseTest {

    @Test
    public void testStreamCapture() {
        System.out.println("Hello world");

        assert getCapturedStdOut().contains("Hello world");

        System.err.println("Goodbye world");
        assert getCapturedStdErr().equals("Goodbye world\n");
    }
}
