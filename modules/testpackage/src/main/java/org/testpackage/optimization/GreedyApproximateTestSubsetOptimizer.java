package org.testpackage.optimization;

import com.googlecode.javaewah.datastructure.BitSet;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.manipulation.Filter;
import org.testpackage.Configuration;
import org.testpackage.pluginsupport.PluginException;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.max;
import static org.testpackage.AnsiSupport.ansiPrintf;

/**
 * @author richardnorth
 */
public class GreedyApproximateTestSubsetOptimizer extends BaseOptimizer implements Optimizer {

    private Integer targetTestCount;
    private Double targetCoverage;
    private Integer targetCost;
    private boolean disabled = false;

    @Override
    public void configure(Configuration configuration) throws PluginException {
        super.configure(configuration);

        if (configuration.getOptimizeTestCoverage() != null) {
            this.targetCoverage = configuration.getOptimizeTestCoverage();
        } else if (configuration.getOptimizeTestRuntimeMillis() != null) {
            this.targetCost = configuration.getOptimizeTestRuntimeMillis();
        } else {
            this.disabled = true;
        }
    }

    @Override
    public Request filterTestRequest(Request request) {

        if (disabled) {
            return request;
        }

        ansiPrintf("@|blue Attempting to select a subset of tests that achieve %s|@\n", describeOptimizationGoal());

        if (testCoverageRepository.isEmpty()) {
            ansiPrintf("@|yellow No coverage data found - test coverage cannot be optimized on this run|@\n");
            ansiPrintf("@|yellow    No coverage data was found in the .testpackage folder|@\n");
            return request;
        }

        Set<TestWithCoverage> coverageSets = new HashSet<TestWithCoverage>();

        this.addCoverageSetsForRootDescription(request.getRunner().getDescription(), coverageSets);
        int maxSize = 0;
        double maxCoverage = 0.0;
        for (TestWithCoverage coverage : coverageSets) {
            maxSize = max(coverage.getCoverage().size(), maxSize);
            maxCoverage = max(coverage.getIndividualCoverage(), maxCoverage);
        }

        if (maxCoverage == 0) {
            ansiPrintf("@|yellow No coverage data found - test coverage cannot be optimized on this run|@\n");
            ansiPrintf("@|yellow    All test methods identified have 0%% coverage:|@\n");
            for (TestWithCoverage coverage : coverageSets) {
                ansiPrintf("     %s @|yellow (%2.1f %%)|@\n", coverage.getId(), coverage.getIndividualCoverage() * 100);
            }
            return request;
        }


        final TestSubsetOptimizerResult optimizerResult = this.solve(coverageSets, maxSize);

        ansiPrintf("@|blue Optimizer complete - plan is %s:|@\n", optimizerResult.describe());
        for (TestWithCoverage selection : optimizerResult.getSelections()) {
            ansiPrintf("  %30s %2.1f%% %s (%dms)\n",
                            selection.getId(),
                            ((double)selection.getCoverage().cardinality() / testCoverageRepository.getNumProbePoints()) * 100,
                            selection.coverageAsString(20, testCoverageRepository.getNumProbePoints()),
                            selection.getCost());
        }
        ansiPrintf("\n\n");

        return request.filterWith(new Filter() {
            @Override
            public boolean shouldRun(Description description) {
               return !description.isTest() || optimizerResult.containsTestName(description.getDisplayName());
            }

            @Override
            public String describe() {
                return "Optimized subset";
            }
        });
    }

    private String describeOptimizationGoal() {
        if (targetTestCount != null) {
            return "best test coverage with exactly " + targetTestCount + " tests run";
        } else if (targetCoverage != null) {
            return String.format("quickest execution time for at least %2.1f%% test coverage", targetCoverage * 100);
        } else if (targetCost != null) {
            return String.format("best test coverage for maximum execution time of %2.1fs", ((double)targetCost / 1000));
        } else {
            throw new IllegalStateException("A target test count or coverage must be set");
        }
    }

    public TestSubsetOptimizerResult solve(Set<TestWithCoverage> coverageSets, int size) {

        List<TestWithCoverage> remainingCandidates = newArrayList(coverageSets);
        List<TestWithCoverage> selections = newArrayList();
        BitSet covered = new BitSet(size);

        if (targetTestCount != null) {
            solveForTargetTestCount(remainingCandidates, selections, covered);
        } else if (targetCoverage != null) {
            solveForTargetCoverage(remainingCandidates, selections, covered);
        } else if (targetCost != null) {
            solveForTargetCost(remainingCandidates, selections, covered);
        } else {
            throw new IllegalStateException("A target test count or coverage must be set");
        }

        return new TestSubsetOptimizerResult(selections, covered, this.testCoverageRepository.getNumProbePoints());
    }

    private void solveForTargetCost(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        int costSoFar = 0;
        while (!remainingCandidates.isEmpty()) {
            BitSet coveredBefore = covered.clone();
            search(remainingCandidates, selections, covered);

            costSoFar += selections.get(selections.size() - 1).getCost();
            if (costSoFar > targetCost) {
                costSoFar -= selections.get(selections.size() - 1).getCost();
                selections.remove(selections.size() - 1);
                covered.and(coveredBefore);
            }
        }

    }

    private void solveForTargetCoverage(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        double coverage = 0;
        while (coverage < targetCoverage && !remainingCandidates.isEmpty()) {

            search(remainingCandidates, selections, covered);
            coverage = ((double) covered.cardinality()) / testCoverageRepository.getNumProbePoints();
        }
    }

    private void solveForTargetTestCount(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        for (int i = 0; i < targetTestCount; i++) {
            search(remainingCandidates, selections, covered);
        }
    }

    private void search(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        int coveredCardinality = covered.cardinality();

        PriorityQueue<Selection> candidates = new PriorityQueue<Selection>();

        // Score each candidate
        for (int j = 0; j < remainingCandidates.size(); j++) {
            final TestWithCoverage candidate = remainingCandidates.get(j);
            BitSet candidateCoverage = candidate.getCoverage();

            // work out the score, the number of newly covered lines divided by the cost of adding this test
            double score = (((double) covered.orcardinality(candidateCoverage)) - coveredCardinality);

            candidates.add(new Selection(score, candidate.getCost(), candidate));
        }

        TestWithCoverage bestCandidate = candidates.peek().candidate;

        if (bestCandidate != null) {
            // Remove the best candidate from future evaluation and add it to the coverage achieved
            remainingCandidates.remove(bestCandidate);
            covered.or(bestCandidate.getCoverage());
            selections.add(bestCandidate);
        }
    }

    public GreedyApproximateTestSubsetOptimizer withTargetTestCount(int targetTestCount) {
        this.targetTestCount = targetTestCount;
        return this;
    }

    public GreedyApproximateTestSubsetOptimizer withTargetTestCoverage(double targetCoverage) {
        this.targetCoverage = targetCoverage;
        return this;
    }

    public GreedyApproximateTestSubsetOptimizer withTargetCost(int targetCost) {
        this.targetCost = targetCost;
        return this;
    }

    private class Selection implements Comparable {
        public final double score;
        public final Long cost;
        public final TestWithCoverage candidate;

        public Selection(double score, Long cost, TestWithCoverage candidate) {

            this.score = score;
            this.cost = cost;
            this.candidate = candidate;
        }

        @Override
        public int compareTo(Object o) {
            Selection that = (Selection) o;

            double thisValue = this.score / this.cost;
            double thatValue = that.score / that.cost;

            if (thisValue > thatValue) {
                return -1;
            } else if (thatValue < thisValue) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
