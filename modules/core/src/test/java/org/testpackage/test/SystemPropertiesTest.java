package org.testpackage.test;

import org.junit.Before;
import org.junit.Test;
import org.testpackage.TestPackage;
import org.testpackage.TestPackageException;

import java.io.File;
import java.io.IOException;

import static org.testpackage.VisibleAssertions.assertEquals;
import static org.testpackage.VisibleAssertions.assertTrue;
import static org.testpackage.VisibleAssertions.fail;

/**
 * Verify we can set system properties from file.
 *
 * @author outofcoffee
 * @see org.testpackage.runnertest.propertiestests.SystemPropertySetTest
 */
public class SystemPropertiesTest extends StreamCaptureBaseTest {

    @Before
    public void setupPackageName() throws IOException {
        System.setProperty("package", "org.testpackage.runnertest.propertiestests");
    }

    /**
     * Verify that a simple properties file can be read and that its values are available to executing tests.
     *
     * @throws IOException
     */
    @Test
    public void testSetProperties_SimpleFormat() throws IOException {
        final File propertiesFile = new File(SystemPropertiesTest.class.getResource("/example-props.properties").getPath());

        final TestPackage testPackage = new TestPackage();
        testPackage.setPropertiesFile(propertiesFile);
        final int exitCode = testPackage.run();

        assertEquals("System property should be set from simple properties file", "testValue", System.getProperty("testProperty"));

        final String capturedStdOut = getCapturedStdOut();
        assertTrue("1 test should have passed", capturedStdOut.contains("1 passed") && capturedStdOut.contains("SystemPropertySetTest"));
        assertTrue("stdout should contain 'TESTS COMPLETE'", capturedStdOut.contains("TESTS COMPLETE"));
        assertEquals("the exit code should be 0", 0, exitCode);
    }

    /**
     * Verify that an XML properties file can be read and that its values are available to executing tests.
     *
     * @throws IOException
     */
    @Test
    public void testSetProperties_XmlFormat() throws IOException {
        final File propertiesFile = new File(SystemPropertiesTest.class.getResource("/example-props.xml").getPath());

        final TestPackage testPackage = new TestPackage();
        testPackage.setPropertiesFile(propertiesFile);
        final int exitCode = testPackage.run();

        assertEquals("System property should be set from XML properties file", "testValue", System.getProperty("testProperty"));

        final String capturedStdOut = getCapturedStdOut();
        assertTrue("1 test should have passed", capturedStdOut.contains("1 passed") && capturedStdOut.contains("SystemPropertySetTest"));
        assertTrue("stdout should contain 'TESTS COMPLETE'", capturedStdOut.contains("TESTS COMPLETE"));
        assertEquals("the exit code should be 0", 0, exitCode);
    }

    /**
     * Verify that {@link TestPackageException} is thrown when the specified properties file cannot be read.
     *
     * @throws IOException
     */
    @Test(expected = TestPackageException.class)
    public void testSetProperties_Exception_FileNotFound() throws IOException {
        final File propertiesFile = new File("non-existent-file.properties");

        final TestPackage testPackage = new TestPackage();
        testPackage.setPropertiesFile(propertiesFile);

        fail("TestPackage should have thrown an exception as the properties file does not exist");
    }
}
