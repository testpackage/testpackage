package org.testpackage.optimization;

import com.googlecode.javaewah.datastructure.BitSet;

/**
 * @author richardnorth
 */
public class TestWithCoverage {

    private String id;
    private BitSet coverage;
    private Long cost;
    private Long numProbePoints;

    public TestWithCoverage(String id, BitSet coverage, Long numProbePoints, Long cost) {
        this.id = id;
        this.coverage = coverage;
        this.cost = cost;
        this.numProbePoints = numProbePoints;
    }

    public static String coverageAsString(TestWithCoverage coverage) {
        final BitSet bitset = coverage.getCoverage();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < coverage.getNumProbePoints(); i++) {
            stringBuffer.append(bitset.get(i) ? "X" : " ");
        }
        return stringBuffer.toString();
    }

    public String getId() {
        return id;
    }

    public BitSet getCoverage() {
        return coverage;
    }

    public Long getCost() {
        return cost;
    }

    public double getIndividualCoverage() {
        return ((double) coverage.cardinality()) / coverage.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestWithCoverage that = (TestWithCoverage) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "TestWithCoverage{" + "id='" + id + '\'' +
                ", individualCoverage=" + getIndividualCoverage() * 100 + "%}";
    }

    public String coverageAsString(int stringLength, long numProbePoints) {
        StringBuilder sb = new StringBuilder("[");
        final int chunkSize = (int) (numProbePoints / (stringLength - 2));
        for (int i=0; i < stringLength-2; i++) {
            int chunkStart = i * chunkSize;
            int chunkEnd = (i+1) * chunkSize;

            if (coverage.nextSetBit(chunkStart) < chunkEnd) {
                sb.append('\u2592');
            } else {
                sb.append(' ');
            }
        }
        sb.append(']');

        return sb.toString();
    }

    public Long getNumProbePoints() {
        return numProbePoints;
    }

    public void setNumProbePoints(Long numProbePoints) {
        this.numProbePoints = numProbePoints;
    }
}
