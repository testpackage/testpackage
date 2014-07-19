package org.testpackage.pluginsupport;

import org.testpackage.Configuration;

/**
 * @author richardnorth
 */
public class AbstractPlugin implements Plugin {

    protected Configuration configuration;

    @Override
    public void configure(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void beforeTest(String testIdentifier) {

    }

    @Override
    public void afterTest(String testIdentifier) {

    }
}
