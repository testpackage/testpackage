package org.testpackage.output;

import org.testpackage.AnsiSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author richardnorth
 */
public class SimpleLogger {

    private final Class clazz;
    private final SimpleDateFormat dateFormatter;

    public static SimpleLogger getLogger(Class clazz) {
        return new SimpleLogger(clazz);
    }

    private SimpleLogger(Class clazz) {

        this.clazz = clazz;
        this.dateFormatter = new SimpleDateFormat("hh:mm:ss.SSS");
    }

    private void log(Level level, String message, Throwable e, Object[] messageParams) {

        final String line = new StringBuilder()
                .append(this.dateFormatter.format(new Date()))
                .append(" @|")
                .append(level.jansiCode)
                .append(" ")
                .append(level.label)
                .append(": ")
                .append(message)
                .append("|@\n").toString();

        AnsiSupport.ansiPrintf(line, messageParams);
    }

    public void success(String message, Object... messageParams) {
        log(Level.SUCCESS, message, null, messageParams);
    }

    public void debug(String message, Object... messageParams) {
        log(Level.DEBUG, message, null, messageParams);
    }

    public void warn(String message, Object... messageParams) {
        log(Level.WARN, message, null, messageParams);
    }

    public void warn(String message, Throwable e, Object... messageParams) {
        log(Level.WARN, message, e, messageParams);
    }

    public void info(String message, Object... messageParams) {
        log(Level.INFO, message, null, messageParams);
    }
}
