package org.testpackage.util;

import com.google.common.io.Resources;
import org.testpackage.TestPackage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.Manifest;

/**
 * Utility class providing access to TestPackage metadata files on the classpath.
 *
 * @author richardnorth
 */
public class Metadata {

    private Metadata() {
        // Utility class - no access
    }

    public static Collection<? extends String> getAllMainAttributes(String attributeName) {
        Enumeration<URL> resources;
        Set<String> results = new HashSet<String>();

        try {
            resources = TestPackage.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        } catch (IOException ignored) {
            return Collections.emptySet();
        }

        while (resources.hasMoreElements()) {
            try {
                URL url = resources.nextElement();
                Manifest manifest = new Manifest(url.openStream());
                String attributes = manifest.getMainAttributes().getValue(attributeName);
                if (attributes != null) {
                    results.add(attributes);
                }
            } catch (IOException ignored) {
            }
        }

        return results;
    }

    public static String getClasspathResourceProperty(String resourceName, String propertyName) {
        final URL resourceURL = Resources.getResource(resourceName);
        if (resourceURL != null) {
            Properties properties = new Properties();
            try {
                properties.load(resourceURL.openStream());
            } catch (IOException e) {
                throw new RuntimeException("Could not open classpath resource: " + resourceName, e);
            }
            if (properties.getProperty(propertyName) != null) {
                return properties.getProperty(propertyName);
            } else {
                throw new IllegalArgumentException("Could not find a property named: " + propertyName + " inside resource: " + resourceName);
            }
        } else {
            throw new IllegalArgumentException("Could not find a classpath resource named: " + resourceName);
        }

    }
}
