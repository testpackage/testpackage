/*
 * Copyright 2013 Deloitte Digital and Richard North
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.testpackage;

import com.google.common.collect.Lists;
import com.twitter.common.testing.runner.AntJunitXmlReportListener;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.StoppedByUserException;
import org.kohsuke.args4j.*;
import org.testpackage.failfast.FailFastRunListener;
import org.testpackage.junitcore.FailFastSupportCore;
import org.testpackage.sequencing.TestHistoryRepository;
import org.testpackage.sequencing.TestHistoryRunListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import static org.testpackage.AnsiSupport.ansiPrintf;
import static org.testpackage.AnsiSupport.initialize;

/**
 * TestPackage main class - should be referenced from a JAR manifest as the Main Class and run from the shell.
 * </p>
 * See @Option-annotated fields for command line switches.
 * </p>
 * If any command line arguments are passed, these are used as the Java package names which should be searched
 * for test classes (not recursive). Otherwise, an attribute named 'TestPackage-Package' is used to identify
 * the right test package name, or failing that, a system property called 'package' is used.
 *
 * @author rnorth
 */
public class TestPackage {

    private static final Logger LOGGER = Logger.getLogger(TestPackage.class.getSimpleName());

    @Option(name = "--failfast", aliases = "-ff", usage = "Fail Fast: Causes test run to be aborted at the first test failure")
    public boolean failFast = false;

    @Option(name = "--quiet", aliases = "-q", usage = "Quiet mode: suppress all output except for test result and counts of pass/failed/ignored tests")
    public boolean quiet = false;

    @Option(name = "--verbose", aliases = "-v", usage = "Verbose mode: display all test output, even for passing tests")
    public boolean verbose = false;

    @Option(name = "--shard", usage = "The index of this test shard (default: 0)")
    private int shardIndex = 0;

    @Option(name = "--numShards", usage = "The total number of shards that tests should be distributed across (default: 1)")
    private int numberOfShards = 1;

    @Argument
    private List<String> testPackageNames = Lists.newArrayList();

    public static void main(String[] args) throws IOException {

        initialize();

        int exitCode = new TestPackage().doMain(args);

        System.exit(exitCode);
    }

    /**
     * Sets or overrides system properties from the specified file.
     * <p/>
     * This is invoked before TestPackage itself is completely initialised so that, in theory, you could use
     * a file to set system properties for TestPackage itself, rather than passing them all as command line arguments.
     * <p/>
     * Using a capitalised 'P' for the alias for consistency with other common Java projects that do this.
     *
     * @param propertiesFile
     */
    @Option(name = "--propertiesfile", aliases = "-P", usage = "Properties File: Sets or overrides system properties from a file")
    public void setPropertiesFile(File propertiesFile) {
        if (!propertiesFile.canRead()) {
            throw new TestPackageException(String.format("Could not read properties file %s", propertiesFile.getAbsolutePath()));
        }

        // read the properties from the file provided and set them as system properties
        InputStream propertiesStream = null;
        try {
            propertiesStream = new FileInputStream(propertiesFile);
            final Properties properties = new Properties();

            if (propertiesFile.getName().toLowerCase().endsWith(".xml")) {
                LOGGER.finest("Attempting to read properties file with XML format");
                properties.loadFromXML(propertiesStream);

            } else {
                LOGGER.finest("Attempting to read properties file with simple format");
                properties.load(propertiesStream);
            }

            /**
             * Don't use System.setProperties() as that will cause <i>all</i> System
             * properties to be overridden.
             */
            for (Object keyObj : properties.keySet()) {
                final String key = (String) keyObj;
                System.setProperty(key, properties.getProperty(key));
            }

        } catch (IOException e) {
            throw new TestPackageException(String.format("Could not read properties file %s", propertiesFile.getAbsolutePath()), e);

        } finally {
            if (null != propertiesStream) {
                try {
                    propertiesStream.close();
                } catch (IOException e) {
                    // sink this
                }
            }
        }
    }

    private int doMain(String[] args) throws IOException {

        CmdLineParser cmdLineParser = new CmdLineParser(this);
        try {
            cmdLineParser.parseArgument(args);
        } catch (CmdLineException e) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java -jar JARFILE [options...] packagenames...");
            // print the list of available options
            cmdLineParser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.println("  Example: java -jar JARFILE" + cmdLineParser.printExample(ExampleMode.ALL) + " packagenames");

            return -1;
        }

        return run();
    }

    public int run() throws IOException {

        validateSettings();

        TestHistoryRepository testHistoryRepository;
        try {
            new File(".testpackage").mkdir();
            testHistoryRepository = new TestHistoryRepository(".testpackage/history.txt");
        } catch (IOException e) {
            throw new TestPackageException("Could not create or open test history repository file at .testpackage/history.txt!", e);
        }
        TestHistoryRunListener testHistoryRunListener = new TestHistoryRunListener(testHistoryRepository);

        getTestPackage();

        if (numberOfShards > 1 && !quiet) {
            ansiPrintf("@|blue Tests will be sharded; this is shard index: %d, number of shards is: %d|@\n", this.shardIndex, this.numberOfShards);
        }

        TestSequencer testSequencer = new TestSequencer(shardIndex, numberOfShards);
        Request request = testSequencer.sequenceTests(testHistoryRepository.getRunsSinceLastFailures(), testPackageNames.toArray(new String[testPackageNames.size()]));

        FailFastSupportCore core = new FailFastSupportCore();

        File targetDir = new File("target");
        boolean mkdirs = targetDir.mkdirs();
        if (!(targetDir.exists() || mkdirs)) {
            throw new TestPackageException("Could not create target directory: " + targetDir.getAbsolutePath());
        }

        ColouredOutputRunListener colouredOutputRunListener = new ColouredOutputRunListener(failFast, verbose, quiet);
        RunListener antXmlRunListener = new AntJunitXmlReportListener(targetDir, colouredOutputRunListener);

        core.addListener(antXmlRunListener);
        core.addListener(colouredOutputRunListener);
        core.addListener(testHistoryRunListener);

        if (failFast) {
            core.addListener(new FailFastRunListener(core.getNotifier()));
        }

        Result result;
        try {
            result = core.run(request);
        } catch (StoppedByUserException e) {
            // Thrown in fail-fast mode
            ansiPrintf("@|red FAILED|@\n");
            return 1;
        } finally {
            testHistoryRepository.save();
        }

        int failureCount = result.getFailureCount();
        int testCount = result.getRunCount();
        int passed = testCount - failureCount;
        if (failureCount > 0) {
            ansiPrintf("@|red FAILED|@\n");
            return 1;
        } else {
            ansiPrintf("@|green OK|@\n");
            return 0;
        }
    }

    private void validateSettings() {
        if (this.quiet && this.verbose) {
            throw new TestPackageException("Quiet and Verbose flags cannot be used simultaneously");
        }

        if (this.shardIndex < 0 || this.shardIndex >= this.numberOfShards) {
            throw new TestPackageException("Shard index should be in the range 0.." + (this.numberOfShards - 1));
        }
    }


    private void getTestPackage() {

        if (testPackageNames.size() != 0) {
            // command-line arguments always preferred
            return;
        }

        // Check the JAR manifest
        try {
            Enumeration<URL> resources = TestPackage.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                Manifest manifest = new Manifest(url.openStream());
                String attributes = manifest.getMainAttributes().getValue("TestPackage-Package");
                if (attributes != null) {
                    testPackageNames.add(attributes);
                    return;
                }
            }

        } catch (IOException e) {
            throw new TestPackageException("Error loading MANIFEST.MF", e);
        }

        // Fall back to system property
        String packageNameSystemProperty = System.getProperty("package");
        if (packageNameSystemProperty != null) {
            testPackageNames.add(packageNameSystemProperty);
        }

        if (testPackageNames.size() == 0) {
            throw new TestPackageException("No package names were set for packages to scan for test classes. " +
                    "Either pass a command line argument, set a system property called 'package', " +
                    "or set an attribute in the built test package JAR's MANIFEST named 'TestPackage-Package'.");
        }
    }

    public void setShardIndex(int shardIndex) {
        this.shardIndex = shardIndex;
    }

    public void setNumberOfShards(int numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

}
