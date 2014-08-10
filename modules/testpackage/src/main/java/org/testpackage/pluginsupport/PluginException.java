package org.testpackage.pluginsupport;

/**
 * @author richardnorth
 */
public class PluginException extends Exception {
    public PluginException(String message, Exception e) {
        super(message, e);
    }
}
