package org.testpackage;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class TestPackageException extends RuntimeException {
    public TestPackageException(String message) {
        super(message);
    }

    public TestPackageException(String message, Exception e) {
        super(message, e);
    }
}
