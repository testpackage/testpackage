package org.testpackage.output;

/**
 * @author richardnorth
 */
public enum Level {
    SUCCESS("SUCCESS", "blue"),
    INFO   ("   INFO", "white"),
    WARN   ("   WARN", "yellow"),
    DEBUG  ("  DEBUG", "white,faint");

    public final String label;
    public final String jansiCode;

    Level(String label, String jansiCode) {

        this.label = label;
        this.jansiCode = jansiCode;
    }
}
