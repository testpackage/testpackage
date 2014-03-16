package org.testpackage;

import org.junit.runner.Description;

import java.util.Comparator;

/**
 * Comparator which orders test descriptions by the name of the test class they belong to.
 *
 * @author richardnorth
 */
public class LexicographicRequestComparator implements Comparator<Description> {
    @Override
    public int compare(Description o1, Description o2) {
        return o1.getClassName().compareTo(o2.getClassName());
    }
}
