package org.testpackage.pluginsupport;

import org.testpackage.Configuration;

/**
 * @author richardnorth
 */
public interface Plugin {
    void configure(Configuration configuration) throws PluginException;

    boolean isActive();

    void beforeTest(String testIdentifier) throws PluginException;

    void afterTest(String testIdentifier) throws PluginException;
}
