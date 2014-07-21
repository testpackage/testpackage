package org.testpackage.output;

import org.junit.runner.Description;

/**
 * @author richardnorth
 */
public class StringRepresentations {
    public static String testName(Description description) {
        return description.getTestClass().getSimpleName() + "." + description.getMethodName();
    }

    public static String testName(Description description, int padRightToLength) {
        return String.format("%-" + padRightToLength + "s", testName(description));
    }
}
