package org.testpackage.util;

import java.util.List;

/**
 * @author richardnorth
 */
public class Throwables2 {
    public static StackTraceElement getLastResponsibleCause(Throwable throwable, List<String> suspectPackageNames) {

        final StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            for (String suspectPackageName : suspectPackageNames) {
                if (stackTraceElement.getClassName().startsWith(suspectPackageName)) {
                    return stackTraceElement;
                }
            }
        }
        return null;
    }
}
