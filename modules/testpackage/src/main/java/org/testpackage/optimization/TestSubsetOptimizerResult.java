package org.testpackage.optimization;

import com.google.common.collect.Sets;
import com.googlecode.javaewah.datastructure.BitSet;

import java.util.List;
import java.util.Set;

/**
 * @author richardnorth
 */
public class TestSubsetOptimizerResult {
    private final List<TestWithCoverage> selections;
    private final BitSet covered;
    private final Set<String> selectedTestNames;
    private Long cost = 0L;

    public TestSubsetOptimizerResult(List<TestWithCoverage> selections, BitSet covered) {

        this.selections = selections;
        this.covered = covered;
        this.selectedTestNames = Sets.newHashSet();

        for (TestWithCoverage testWithCoverage : selections) {
            this.selectedTestNames.add(testWithCoverage.getId());
            this.cost += testWithCoverage.getCost();
        }
    }

    public List<TestWithCoverage> getSelections() {
        return selections;
    }

    public BitSet getCovered() {
        return covered;
    }

    public int getCoveredLines() {
        return covered.cardinality();
    }

    public boolean containsTestName(String testName) {
        return this.selectedTestNames.contains(testName);
    }

    public String describe() {
        return String.format("%d tests with %dms expected execution time", this.selectedTestNames.size(), this.cost);
    }
}
