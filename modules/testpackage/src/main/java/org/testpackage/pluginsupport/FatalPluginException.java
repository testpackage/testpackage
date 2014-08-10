package org.testpackage.pluginsupport;

/**
 * @author richardnorth
 */
public class FatalPluginException extends PluginException {
    public FatalPluginException(String message, Exception e) {
        super(message, e);
    }
}
