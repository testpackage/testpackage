package org.testpackage.streams;

import org.apache.commons.io.output.TeeOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Stack;

/**
 * Created by richardnorth on 05/01/2014.
 */
public class StreamCapture {

    private static Stack<StreamCapture> chain = new Stack<StreamCapture>();
    private static RedirectableOutputStream redirectableSystemOut;
    private static RedirectableOutputStream redirectableSystemErr;

    static {
        // The base filter instance should always tee output to System out/err
        PrintStream initialSystemOut = System.out;
        PrintStream initialSystemErr = System.err;
        chain.push(new StreamCapture(initialSystemOut, initialSystemErr, "Root"));

        // Intercept all future output to System out/err so that we can redirect to the topmost filter instance later
        //  Required for intercepting static Loggers which grab a reference to the system streams only once
        redirectableSystemOut = new RedirectableOutputStream(System.out);
        redirectableSystemErr = new RedirectableOutputStream(System.err);
        System.setOut(new PrintStream(redirectableSystemOut));
        System.setErr(new PrintStream(redirectableSystemErr));
    }

    private final ByteArrayOutputStream outBaos = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
    private final PrintStream out;
    private final PrintStream err;
    private final String name;

    private StreamCapture(PrintStream parentOut, PrintStream parentErr, String name) {
        this.name = name;
        this.out = new PrintStream(new TeeOutputStream(parentOut, outBaos));
        this.err = new PrintStream(new TeeOutputStream(parentErr, errBaos));
    }

    private StreamCapture(String name) {
        this.name = name;
        this.out = new PrintStream(outBaos);
        this.err = new PrintStream(errBaos);
    }

    public synchronized static StreamCapture grabStreams(boolean teeOutput, String name) {

        final StreamCapture streamCapture;
        if (teeOutput) {
            streamCapture = new StreamCapture(chain.peek().out, chain.peek().err, name);
        } else {
            streamCapture = new StreamCapture(name);
        }

        chain.push(streamCapture);

//        initialSystemOut.println("Pushed to chain, now: " + chain);

        applyToSystemStreams();

        return streamCapture;
    }

    public static void restore() {

        // Flush to try and ensure all output messages get to the appropriate buffer before switching
        System.out.flush();
        System.err.flush();

        if (chain.size() > 0) {
            chain.pop();
        }

//        initialSystemOut.println("Popped chain, now: " + chain);

        applyToSystemStreams();
    }

    private static void applyToSystemStreams() {

        /* Have to take control of System out/err every time for TestPackage unit tests
         *  run by Gradle rather than the TestPackage binary. Gradle overrides System out
         *  and err in between tests, so we wrest control back here. Should have no ill
         *  effect when done in other contexts.
         */
        System.setOut(new PrintStream(redirectableSystemOut));
        System.setErr(new PrintStream(redirectableSystemErr));

        /*
         * Point our redirectable streams at the topmost filter in the chain.
         */
        if (chain.size() > 0) {
            redirectableSystemOut.setOutputStream(chain.peek().out);
            redirectableSystemErr.setOutputStream(chain.peek().err);
        }
    }

    public String getStdOut() {
        return this.outBaos.toString();
    }

    public String getStdErr() {
        return this.errBaos.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StreamCapture{");
        sb.append("name='").append(name).append('\'');
        sb.append(", errBaos=").append(errBaos.toString().substring(0, Math.min(30, errBaos.toString().length())));
        sb.append(", outBaos=").append(outBaos.toString().substring(0, Math.min(30, errBaos.toString().length())));
        sb.append(", err=").append(err);
        sb.append(", out=").append(out);
        sb.append('}');
        return sb.toString();
    }

    public static int depth() {
        return chain.size();
    }
}
