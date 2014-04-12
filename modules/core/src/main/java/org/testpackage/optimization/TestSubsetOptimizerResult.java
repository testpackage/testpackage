package org.testpackage.optimization;

import com.googlecode.javaewah.datastructure.BitSet;
import java.util.List;

/**
 * @author richardnorth
 */
public class TestSubsetOptimizerResult {
    private final List<TestWithCoverage> selections;
    private final BitSet covered;

    public TestSubsetOptimizerResult(List<TestWithCoverage> selections, BitSet covered) {

        this.selections = selections;
        this.covered = covered;
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
}
