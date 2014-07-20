package org.testpackage.optimization;

import org.junit.runner.Description;
import org.testpackage.Configuration;

import java.util.Set;

/**
 * @author richardnorth
 */
public class BaseOptimizer {
    protected final TestCoverageRepository testCoverageRepository;

    public BaseOptimizer(Configuration configuration) {

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
