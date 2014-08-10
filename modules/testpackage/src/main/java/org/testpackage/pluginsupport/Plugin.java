package org.testpackage.pluginsupport;

import org.junit.runner.Request;
import org.testpackage.Configuration;

/**
 * @author richardnorth
 */
public interface Plugin {
    void configure(Configuration configuration) throws PluginException;

    boolean isActive();

    void beforeTest(String testIdentifier) throws PluginException;

    void afterTest(String testIdentifier) throws PluginException;

    Request filterTestRequest(Request request);

    String messageDuringTest(String testIdentifier);

    String messageAfterTest(String testIdentifier);
}
