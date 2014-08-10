package org.testpackage.pluginsupport;

import org.junit.runner.Request;
import org.testpackage.Configuration;

/**
 * @author richardnorth
 */
public class AbstractPlugin implements Plugin {

    protected Configuration configuration;

    @Override
    public void configure(Configuration configuration) throws PluginException {
        this.configuration = configuration;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void beforeTest(String testIdentifier) throws PluginException {

    }

    @Override
    public void afterTest(String testIdentifier) throws PluginException {

    }

    @Override
    public Request filterTestRequest(Request request) {
        return request;
    }

    @Override
    public String messageDuringTest(String testIdentifier) {
        return "";
    }

    @Override
    public String messageAfterTest(String testIdentifier) {
        return "";
    }
}
