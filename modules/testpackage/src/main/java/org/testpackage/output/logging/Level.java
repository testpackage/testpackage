package org.testpackage.output.logging;

/**
 * Log levels for SimpleLogger.
 *
 * @author richardnorth
 */
enum Level {
    COMPLETE("COMPLETE", "blue"),
    INFO    ("    INFO", "white"),
    WARN    ("    WARN", "yellow"),
    DEBUG   ("   DEBUG", "white,faint"),
    ERROR   ("   ERROR", "red");

    public final String label;
    public final String jansiCode;

    Level(String label, String jansiCode) {

        this.label = label;
        this.jansiCode = jansiCode;
    }
}
