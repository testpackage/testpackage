package org.testpackage.example.maven.subpackage;

import org.junit.Test;

import static org.rnorth.visibleassertions.VisibleAssertions.assertTrue;

/**
 * Created by richardnorth on 01/01/2014.
 */
public class BasicTest {

    @Test
    public void testBasicTrueAssertion() {
        assertTrue("it should be true", true);
    }
}
