package org.testpackage.runnertest.propertiestests;

import org.junit.Test;

/**
 * Used by {@link org.testpackage.test.SystemPropertiesTest}.
 *
 * @author outofcoffee
 */
public class SystemPropertySetTest {

    @Test
    public void passingTest() {
        // value should have been read from properties file
        assert "testValue".equals(System.getProperty("testProperty"));
    }
}
