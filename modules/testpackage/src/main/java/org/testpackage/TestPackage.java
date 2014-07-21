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

import com.twitter.common.testing.runner.AntJunitXmlReportListener;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.StoppedByUserException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.testpackage.failfast.FailFastRunListener;
import org.testpackage.junitcore.FailFastSupportCore;
import org.testpackage.optimization.TestCoverageRepository;
import org.testpackage.pluginsupport.Plugin;
import org.testpackage.pluginsupport.PluginException;
import org.testpackage.pluginsupport.PluginFacadeRunListener;
import org.testpackage.pluginsupport.PluginManager;
import org.testpackage.sequencing.TestHistoryRepository;
import org.testpackage.sequencing.TestHistoryRunListener;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.testpackage.AnsiSupport.ansiPrintf;
import static org.testpackage.AnsiSupport.initialize;

/**
 * <p>
 * TestPackage main class - should be referenced from a JAR manifest as the Main Class and run from the shell.
 * </p>
 * <p>
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

    private PluginManager pluginManager;
    private Configuration configuration = new Configuration();

    public static void main(String[] args) throws IOException {

        initialize();

        int exitCode = new TestPackage().doMain(args);

        System.exit(exitCode);
    }


    private int doMain(String[] args) throws IOException {

        ansiPrintf("TestPackage standalone test runner initializing\n");
        ansiPrintf("  http://testpackage.org - Version %s\n", "0.2");

        CmdLineParser cmdLineParser = new CmdLineParser(configuration);
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

        try {
            this.pluginManager = new PluginManager(configuration);
        } catch (PluginException e) {
            System.err.println("Error initializing TestPackage plugin: " + e.getMessage());
            return -1;
        }

        return run();
    }

    public int run() throws IOException {

        validateSettings();

        TestHistoryRepository testHistoryRepository;
        TestCoverageRepository testCoverageRepository;
        try {
            new File(".testpackage").mkdir();
            testHistoryRepository = new TestHistoryRepository(".testpackage/history.txt");
            testCoverageRepository = new TestCoverageRepository(".testpackage/coverage.dat");
            configuration.setTestCoverageRepository(testCoverageRepository);
        } catch (IOException e) {
            throw new TestPackageException("Could not create or open test repository file in .testpackage directory!", e);
        } catch (ClassNotFoundException e) {
            throw new TestPackageException("Could not open existing coverage data", e);
        }
        TestHistoryRunListener testHistoryRunListener = new TestHistoryRunListener(testHistoryRepository);

        if (configuration.getNumberOfShards() > 1 && !configuration.isQuiet()) {
            ansiPrintf("@|blue Tests will be sharded; this is shard index: %d, number of shards is: %d|@\n", configuration.getShardIndex(), configuration.getNumberOfShards());
        }

        TestSequencer testSequencer = new TestSequencer(configuration.getShardIndex(), configuration.getNumberOfShards());
        Request request = testSequencer.sequenceTests(testHistoryRepository.getRunsSinceLastFailures(), configuration.getTestPackageNames().toArray(new String[configuration.getTestPackageNames().size()]));

        for (Plugin plugin : this.pluginManager.getPlugins()) {
            request = plugin.filterTestRequest(request);
        }

        FailFastSupportCore core = new FailFastSupportCore();

        File targetDir = new File("target");
        boolean mkdirs = targetDir.mkdirs();
        if (!(targetDir.exists() || mkdirs)) {
            throw new TestPackageException("Could not create target directory: " + targetDir.getAbsolutePath());
        }

        ColouredOutputRunListener colouredOutputRunListener = new ColouredOutputRunListener(configuration.isFailFast(),
                configuration.isVerbose(), configuration.isQuiet(), request.getRunner().testCount(), pluginManager.getPlugins());
        RunListener antXmlRunListener = new AntJunitXmlReportListener(targetDir, colouredOutputRunListener);

        core.addListener(antXmlRunListener);
        core.addListener(colouredOutputRunListener);
        core.addListener(testHistoryRunListener);
        core.addListener(new PluginFacadeRunListener(this.pluginManager.getPlugins()));

        if (configuration.isFailFast()) {
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
            testCoverageRepository.save();
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
        if (configuration.isQuiet() && configuration.isVerbose()) {
            throw new TestPackageException("Quiet and Verbose flags cannot be used simultaneously");
        }

        if (configuration.getShardIndex() < 0 || configuration.getShardIndex() >= configuration.getNumberOfShards()) {
            throw new TestPackageException("Shard index should be in the range 0.." + (configuration.getNumberOfShards() - 1));
        }
    }


    public Configuration getConfiguration() {
        return configuration;
    }
}
