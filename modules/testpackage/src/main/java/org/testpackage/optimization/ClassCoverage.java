package org.testpackage.optimization;

import com.googlecode.javaewah.datastructure.BitSet;

/**
 * @author richardnorth
 */
public class ClassCoverage {

    private final String classIdentifier;
    private final BitSet probePoints;
    private final int probePointCount;
    private final Long executionTime;

    public ClassCoverage(String classIdentifier, BitSet probePoints, int probePointCount, Long executionTime) {
        this.classIdentifier = classIdentifier;
        this.probePoints = probePoints;
        this.probePointCount = probePointCount;
        this.executionTime = executionTime;
    }

    public String getClassIdentifier() {
        return classIdentifier;
    }

    public BitSet getProbePoints() {
        return probePoints;
    }

    public int getProbePointCount() {
        return probePointCount;
    }

    public static BitSet coverageFromString(String stringRepresentation) {
        final BitSet bitset = new BitSet(stringRepresentation.length());
        for (int i = 0; i < stringRepresentation.length(); i++) {
            bitset.set(i, stringRepresentation.charAt(i) != ' ');
        }
        return bitset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassCoverage that = (ClassCoverage) o;

        if (probePointCount != that.probePointCount) return false;
        if (!classIdentifier.equals(that.classIdentifier)) return false;
        if (!probePoints.equals(that.probePoints)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = classIdentifier.hashCode();
        result = 31 * result + probePoints.hashCode();
        result = 31 * result + probePointCount;
        return result;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

}
