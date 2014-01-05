package com.deloittedigital.testpackage.test;

import com.deloittedigital.testpackage.streams.StreamCapture;
import org.junit.Before;

import java.io.IOException;

/**
 * Created by richardnorth on 20/12/2013.
 */
public abstract class StreamCaptureBaseTest {

    private StreamCapture streamCapture;

    @Before
    public void setupStreamCapturing() throws IOException {
        streamCapture = StreamCapture.grabStreams(true);
    }

    protected String getCapturedStdOut() {
        return streamCapture.getStdOut();
    }

    protected String getCapturedStdErr() {
        return streamCapture.getStdErr();
    }


}
