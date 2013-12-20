package com.deloittedigital.testpackage;

import org.apache.commons.io.output.TeeOutputStream;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by richardnorth on 20/12/2013.
 */
public abstract class StreamCaptureBaseTest {

    private static PrintStream originalOut;
    private static PrintStream originalErr;
    private PrintStream outInterceptor;
    private PrintStream errInterceptor;
    private ByteArrayOutputStream outBaos;
    private ByteArrayOutputStream errBaos;


    @BeforeClass
    public static void rememberStreams() {
        originalOut = System.out;
        originalErr = System.err;
    }

    @Before
    public void setup() throws IOException {
        outBaos = new ByteArrayOutputStream();
        errBaos = new ByteArrayOutputStream();
        outInterceptor = new PrintStream(new TeeOutputStream(outBaos, originalOut));
        errInterceptor = new PrintStream(new TeeOutputStream(errBaos, originalErr));
        System.setOut(outInterceptor);
        System.setErr(errInterceptor);
    }

    protected String getCapturedStdOut() {
        return outBaos.toString();
    }

    protected String getCapturedStdErr() {
        return errBaos.toString();
    }


}
