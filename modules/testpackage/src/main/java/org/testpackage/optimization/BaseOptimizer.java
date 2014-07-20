package org.testpackage.optimization;

import org.junit.runner.Description;
import org.testpackage.Configuration;
import org.testpackage.pluginsupport.AbstractPlugin;
import org.testpackage.pluginsupport.PluginException;

import java.util.Set;

/**
 * @author richardnorth
 */
public class BaseOptimizer extends AbstractPlugin {
    protected TestCoverageRepository testCoverageRepository;

    @Override
    public void configure(Configuration configuration) throws PluginException {
        super.configure(configuration);
        this.testCoverageRepository = configuration.getTestCoverageRepository();
    }

    public void addCoverageSetsForRootDescription(Description description, Set<TestWithCoverage> coverageSets) {
        if (description.isTest()) {
            final TestWithCoverage coverage = testCoverageRepository.getCoverage(description.getDisplayName());
            coverageSets.add(coverage);
        } else {
            for (Description childDescription : description.getChildren()) {
                addCoverageSetsForRootDescription(childDescription, coverageSets);
            }
        }
    }
}
