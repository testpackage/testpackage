package org.testpackage.optimization;

import java.util.BitSet;

/**
 * @author richardnorth
 */
public class TestWithCoverage {

    private String id;
    private BitSet coverage;
    private int cost;
    private double individualCoverage;

    public TestWithCoverage(String id, BitSet coverage, int cost) {
        this.id = id;
        this.coverage = coverage;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public BitSet getCoverage() {
        return coverage;
    }

    public int getCost() {
        return cost;
    }

    public double getIndividualCoverage() {
        if (individualCoverage == 0) {
            individualCoverage = ((double) coverage.cardinality()) / coverage.size();
        }
        return individualCoverage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestWithCoverage that = (TestWithCoverage) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestWithCoverage{");
        sb.append("id='").append(id).append('\'');
        sb.append(", individualCoverage=").append(getIndividualCoverage() * 100);
        sb.append("%}");
        return sb.toString();
    }
}
