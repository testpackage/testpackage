package com.deloittedigital.testpackage;

import org.fusesource.jansi.AnsiOutputStream;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author rnorth
 */
public class AnsiSupport {
    protected static PrintStream out;
    protected static PrintStream err;
    private static boolean initialized = false;

    static {
        initialize();
    }

    protected synchronized static void initialize() {
        if (!initialized) {
            // By default we assume your Unix tty can handle ANSI codes.
            // Just wrap it up so that when we get closed, we reset the
            // attributes.
            out = wrapStream(System.out);
            err = wrapStream(System.err);

            initialized = true;
        }
    }

    private static PrintStream wrapStream(final PrintStream stream) {
        return new PrintStream(new FilterOutputStream(stream) {
            @Override
            public void close() throws IOException {
                write(AnsiOutputStream.REST_CODE);
                flush();
                super.close();
            }
        });
    }

    public static void ansiPrintf(String s, Object... args) {
        System.out.println(ansi().render(String.format(s, args)));
    }
}
