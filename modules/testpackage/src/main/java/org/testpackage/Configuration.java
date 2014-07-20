package org.testpackage;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;
import org.testpackage.optimization.TestCoverageRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author richardnorth
 */
public class Configuration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getSimpleName());

    @Option(name = "--failfast", aliases = "-ff", usage = "Fail Fast: Causes test run to be aborted at the first test failure")
    private boolean failFast = false;

    @Option(name = "--quiet", aliases = "-q", usage = "Quiet mode: suppress all output except for test result and counts of pass/failed/ignored tests")
    private boolean quiet = false;

    @Option(name = "--verbose", aliases = "-v", usage = "Verbose mode: display all test output, even for passing tests")
    private boolean verbose = false;

    @Option(name = "--shard", usage = "The index of this test shard (default: 0)")
    private int shardIndex = 0;

    @Option(name = "--numShards", usage = "The total number of shards that tests should be distributed across (default: 1)")
    private int numberOfShards = 1;

    @Option(name = "--jacoco-host", usage = "The hostname of a JaCoCo agent running within the system under test (Java only, default: localhost")
    private String jaCoCoAgentHostName = "localhost";

    @Option(name = "--jacoco-port", usage = "The port number of a JaCoCo agent running within the system under test (Java only, default: 6300")
    private int jaCoCoAgentPort = 6300;

    @Option(name = "--optimize-coverage", usage = "The target test coverage to optimize towards if coverage data is available (e.g. 80%)")
    private String optimizeTestCoverage;

    @Option(name = "--optimize-time", usage = "The test execution time to optimize towards if coverage data is available (e.g. 5m, 2m30s)")
    private String optimizeTestRuntime;

    @Option(name = "--optimize-count", usage = "The number of tests to optimize towards if coverage data is available (e.g. 20)")
    private int optimizeTestCount;

    @Option(name = "--jacoco-user-package-prefix", usage = "The root java package name for classes we want to optimize coverage for")
    private String jaCoCoUserPackagePrefix = "";


    @Argument
    private List<String> testPackageNames = Lists.newArrayList();
    private TestCoverageRepository testCoverageRepository;

    /**
     * <p>
     * Sets or overrides system properties from the specified file.
     * </p>
     * <p>
     * This is invoked before TestPackage itself is completely initialised so that, in theory, you could use
     * a file to set system properties for TestPackage itself, rather than passing them all as command line arguments.
     * </p>
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

    public boolean isFailFast() {
        return failFast;
    }

    public boolean isQuiet() {
        return quiet;
    }


    public boolean isVerbose() {
        return verbose;
    }


    public int getShardIndex() {
        return shardIndex;
    }


    public int getNumberOfShards() {
        return numberOfShards;
    }


    public String getJaCoCoAgentHostName() {
        return jaCoCoAgentHostName;
    }

    public int getJaCoCoAgentPort() {
        return jaCoCoAgentPort;
    }

    public Double getOptimizeTestCoverage() {

        Double result = null;
        if (!Strings.isNullOrEmpty(this.optimizeTestCoverage)) {
            final Pattern regex = Pattern.compile("([0-9\\.]+)%?");
            final Matcher matcher = regex.matcher(this.optimizeTestCoverage);
            if (matcher.matches()) {
                try {
                    result = Double.parseDouble(matcher.group(1)) / 100;
                } catch (NumberFormatException ignored) {}
            }
        }

        return result;
    }

    public Integer getOptimizeTestRuntimeMillis() {
        Double result = null;
        if (!Strings.isNullOrEmpty(this.optimizeTestRuntime)) {
            double amount = 0.0;
            final Pattern regex = Pattern.compile("([0-9\\.]+)(h|m|min(s?)|s|ms)");
            final Matcher matcher = regex.matcher(this.optimizeTestRuntime);
            if (matcher.matches()) {
                try {
                    amount = Double.parseDouble(matcher.group(1));
                    final String unitString = matcher.group(2);
                    if ("h".equals(unitString)) {
                        result = amount * 60 * 60 * 1000;
                    } else if (!"ms".equals(unitString) && unitString != null && unitString.startsWith("m")) {
                        result = amount * 60 * 1000;
                    } else if ("s".equals(unitString)) {
                        result = amount * 1000;
                    } else {
                        result = amount;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        if (result != null) {
            return result.intValue();
        }

        return null;
    }

    public int getOptimizeTestCount() {
        return optimizeTestCount;
    }

    public String getJaCoCoUserPackagePrefix() {
        return jaCoCoUserPackagePrefix;
    }

    public List<String> getTestPackageNames() {
        return testPackageNames;
    }

    public TestCoverageRepository getTestCoverageRepository() {
        return testCoverageRepository;
    }

    public void setTestCoverageRepository(TestCoverageRepository testCoverageRepository) {
        this.testCoverageRepository = testCoverageRepository;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setShardIndex(int shardIndex) {
        this.shardIndex = shardIndex;
    }

    public void setNumberOfShards(int numberOfShards) {
        this.numberOfShards = numberOfShards;
    }
}
