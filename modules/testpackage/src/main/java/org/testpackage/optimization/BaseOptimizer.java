package org.testpackage.optimization;

import org.junit.runner.Description;
import org.testpackage.Configuration;
import org.testpackage.output.StringRepresentations;
import org.testpackage.pluginsupport.AbstractPlugin;
import org.testpackage.pluginsupport.PluginException;

import java.util.Set;

/**
 * @author richardnorth
 */
public class BaseOptimizer extends AbstractPlugin {

    @Override
    public void configure(Configuration configuration) throws PluginException {
        super.configure(configuration);
    }

    public void addCoverageSetsForRootDescription(Description description, Set<TestWithCoverage> coverageSets) {
        if (description.isTest()) {
            final TestWithCoverage coverage = getTestCoverageRepository().getCoverage(StringRepresentations.testName(description));
            coverageSets.add(coverage);
        } else {
            for (Description childDescription : description.getChildren()) {
                addCoverageSetsForRootDescription(childDescription, coverageSets);
            }
        }
    }

    protected TestCoverageRepository getTestCoverageRepository() {
        return configuration.getTestCoverageRepository();
    }
}
