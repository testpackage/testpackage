package org.testpackage.streams;

import org.apache.commons.io.output.TeeOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by richardnorth on 05/01/2014.
 */
public class StreamCapture {

    private static boolean initialized;
    private static PrintStream originalOut;
    private static PrintStream originalErr;

    private PrintStream outInterceptor;
    private PrintStream errInterceptor;
    private ByteArrayOutputStream outBaos;
    private ByteArrayOutputStream errBaos;

    private StreamCapture(boolean teeOutput) {

        initialize();

        outBaos = new ByteArrayOutputStream();
        errBaos = new ByteArrayOutputStream();
        if (teeOutput) {
            outInterceptor = new PrintStream(new TeeOutputStream(outBaos, originalOut));
            errInterceptor = new PrintStream(new TeeOutputStream(errBaos, originalErr));
        } else {
            outInterceptor = new PrintStream(outBaos);
            errInterceptor = new PrintStream(errBaos);
        }
        System.setOut(outInterceptor);
        System.setErr(errInterceptor);
    }

    public synchronized static StreamCapture grabStreams(boolean teeOutput) {

        if (!initialized) {
            initialize();
        }
        return new StreamCapture(teeOutput);
    }

    private static void initialize() {
        originalOut = System.out;
        originalErr = System.err;
        initialized = true;
    }

    public String getStdOut() {
        return outBaos.toString();
    }

    public String getStdErr() {
        return errBaos.toString();
    }

    public void restore() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        initialized = false;
    }
}
