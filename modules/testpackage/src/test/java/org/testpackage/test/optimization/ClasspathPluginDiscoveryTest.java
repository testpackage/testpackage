package org.testpackage.test.optimization;

import org.junit.Test;
import org.testpackage.Configuration;
import org.testpackage.pluginsupport.Plugin;
import org.testpackage.pluginsupport.PluginException;
import org.testpackage.pluginsupport.PluginManager;

import java.util.Collection;

import static org.rnorth.visibleassertions.VisibleAssertions.fail;


/**
 * @author richardnorth
 */
public class ClasspathPluginDiscoveryTest {

    @Test
    public void testListenersFound() throws PluginException {
        PluginManager pluginManager = new PluginManager(new Configuration());
        final Collection<? extends Plugin> plugins = pluginManager.getPlugins();

        for (Plugin plugin : plugins) {
            if (plugin instanceof DummyPlugin) {
                return; // Found expected
            }
        }
        fail("Expected dummy plugin not found");
    }
}
