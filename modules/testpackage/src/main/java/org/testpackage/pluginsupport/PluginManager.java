package org.testpackage.pluginsupport;

import org.testpackage.Configuration;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author richardnorth
 */
public class PluginManager {

    private final Set<Plugin> plugins = new LinkedHashSet<Plugin>();

    public PluginManager(Configuration configuration) throws PluginException {
        this();

        for (Plugin plugin : plugins) {
            plugin.configure(configuration);
        }
    }

    private PluginManager() {
        ServiceLoader<Plugin> serviceLoader = ServiceLoader.load(Plugin.class);
        for (Plugin plugin : serviceLoader) {
            plugins.add(plugin);
        }
    }

    public Collection<? extends Plugin> getPlugins() {
        return plugins;
    }
}
