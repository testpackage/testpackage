package org.testpackage.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.testpackage.streams.StreamCapture;

import java.io.IOException;

/**
 * Created by richardnorth on 20/12/2013.
 */
public abstract class StreamCaptureBaseTest {

    private StreamCapture streamCapture;

    @Before
    public void setupStreamCapturing() throws IOException {
//        System.out.println("SC will be grabbed");
        streamCapture = StreamCapture.grabStreams(false, "StreamCaptureBaseTest");
    }

    @After
    public void endStreamCapturing() {

        StreamCapture.restore();
//        System.out.println("SC restored: " + getCapturedStdOut());
    }

    @AfterClass
    public static void outputEvents() {
        ///System.err.println(EventDebugger.events);
    }

    protected String getCapturedStdOut() {
        System.out.flush();
        return streamCapture.getStdOut();
    }

    protected String getCapturedStdErr() {
        System.err.flush();
        return streamCapture.getStdErr();
    }


}
