package org.testpackage.output;

import org.junit.runner.Description;

/**
 * @author richardnorth
 */
public class StringRepresentations {
    public static String testName(Description description) {
        if (! description.isTest() && description.isSuite() && description.getChildren().size() > 0) {
            final Description firstDescription = description.getChildren().get(0);
            return firstDescription.getClassName();
        }
        return description.getTestClass().getSimpleName() + "." + description.getMethodName();
    }

    public static String testName(Description description, int padRightToLength) {
        return String.format("%-" + padRightToLength + "s", testName(description));
    }
}
