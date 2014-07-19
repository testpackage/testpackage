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

        this.testCoverageRepository = configuration.testCoverageRepository;
    }

    public void addCoverageSetsForRootDescription(Description description, Set<TestWithCoverage> coverageSets) {
        if (description.isTest()) {
            final ClassCoverage coverage = testCoverageRepository.getCoverage(description.getDisplayName());
            coverageSets.add(new TestWithCoverage(description.getDisplayName(), coverage.getProbePoints(), coverage.getExecutionTime()));
        } else {
            for (Description childDescription : description.getChildren()) {
                addCoverageSetsForRootDescription(childDescription, coverageSets);
            }
        }
    }
}
