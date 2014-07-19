package org.testpackage.pluginsupport;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

import java.util.Collection;

/**
 * @author richardnorth
 */
public class PluginFacadeRunListener extends RunListener {
    private final Collection<? extends Plugin> plugins;

    public PluginFacadeRunListener(Collection<? extends Plugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public void testStarted(Description description) throws Exception {
        for (Plugin plugin : plugins) {
            if (plugin.isActive()) {
                plugin.beforeTest(description.getDisplayName());
            }
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {
        for (Plugin plugin : plugins) {
            if (plugin.isActive()) {
                plugin.afterTest(description.getDisplayName());
            }
        }
    }
}
