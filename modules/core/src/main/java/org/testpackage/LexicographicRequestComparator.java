package org.testpackage;

import java.util.Comparator;

/**
 * Comparator which orders test classes by name.
 *
 * @author richardnorth
 */
public class LexicographicRequestComparator implements Comparator<Class> {
    @Override
    public int compare(Class o1, Class o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
