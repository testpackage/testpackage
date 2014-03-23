package org.testpackage.optimization;

import java.util.BitSet;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author richardnorth
 */
public class GreedyApproximateTestSubsetOptimizer {

    public TestSubsetOptimizerResult solve(List<TestWithCoverage> coverageSets, int targetTestCount) {

        List<TestWithCoverage> remainingCandidates = newArrayList(coverageSets);
        List<TestWithCoverage> selections = newArrayList();
        BitSet covered = new BitSet();

        for (int i = 0; i < targetTestCount; i++) {

            double bestScore = 0;
            TestWithCoverage bestCandidate = null;

            // Score each candidate
            for (int j = 0; j < remainingCandidates.size(); j++) {
                final TestWithCoverage candidate = remainingCandidates.get(j);
                BitSet candidateCoverage = candidate.getCoverage();

                // work out which lines of code get freshly covered
                covered.xor(candidateCoverage);

                // work out the score, the number of newly covered lines
                double score = ((double) covered.cardinality()) / candidate.getCost();

                // revert covered back to its previous state
                covered.xor(candidateCoverage);

                if (score > bestScore) {
                    bestScore = score;
                    bestCandidate = candidate;
                }
            }

            if (bestCandidate != null) {
                // Remove the best candidate from future evaluation and add it to the coverage achieved
                remainingCandidates.remove(bestCandidate);
                covered.or(bestCandidate.getCoverage());
                selections.add(bestCandidate);
            }
        }

        return new TestSubsetOptimizerResult(selections, covered);
    }
}
