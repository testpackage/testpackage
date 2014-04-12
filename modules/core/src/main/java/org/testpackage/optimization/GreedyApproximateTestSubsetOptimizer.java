package org.testpackage.optimization;

import com.googlecode.javaewah.datastructure.BitSet;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author richardnorth
 */
public class GreedyApproximateTestSubsetOptimizer {

    private Integer targetTestCount;
    private Double targetCoverage;
    private Integer targetCost;

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


        return new TestSubsetOptimizerResult(selections, covered);
    }

    private void solveForTargetCost(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        int costSoFar = 0;
        while (!remainingCandidates.isEmpty()) {
            search(remainingCandidates, selections, covered);

            costSoFar += selections.get(selections.size() - 1).getCost();
            if (costSoFar > targetCost) {
                selections.remove(selections.size() - 1);
                break;
            }
        }

    }

    private void solveForTargetCoverage(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        double coverage = 0;
        while (coverage < targetCoverage && !remainingCandidates.isEmpty()) {

            search(remainingCandidates, selections, covered);
            coverage = ((double) covered.cardinality()) / covered.size();
        }
    }

    private void solveForTargetTestCount(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        for (int i = 0; i < targetTestCount; i++) {
            search(remainingCandidates, selections, covered);
        }
    }

    private void search(List<TestWithCoverage> remainingCandidates, List<TestWithCoverage> selections, BitSet covered) {
        double bestScore = 0;
        TestWithCoverage bestCandidate = null;
        int coveredCardinality = covered.cardinality();

        // Score each candidate
        for (int j = 0; j < remainingCandidates.size(); j++) {
            final TestWithCoverage candidate = remainingCandidates.get(j);
            BitSet candidateCoverage = candidate.getCoverage();

            // work out the score, the number of newly covered lines divided by the cost of adding this test
            double score = (((double) covered.orcardinality(candidateCoverage)) - coveredCardinality) / candidate.getCost();

            if (score > bestScore) {
                bestScore = score;
                bestCandidate = candidate;
                coveredCardinality = covered.cardinality();
            }
        }

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
}
