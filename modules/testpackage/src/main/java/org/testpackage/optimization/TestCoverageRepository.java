package org.testpackage.optimization;

import com.google.common.collect.Maps;
import com.googlecode.javaewah.IntIterator;
import com.googlecode.javaewah.datastructure.BitSet;

import java.io.*;
import java.util.Map;

import static java.lang.Math.max;

/**
 * @author richardnorth
 */
public class TestCoverageRepository {

    private final File backingFile;

    private Map<String, ClassProperties> classProperties = Maps.newHashMap();
    private int size = 0;

    private Map<String, BitSet> testCoverages = Maps.newHashMap();
    private Map<String, Long> testExecutionTimes = Maps.newHashMap();
    private boolean empty = true;
    private long numProbePoints;

    public TestCoverageRepository(String absolutePath) throws ClassNotFoundException, IOException {

        backingFile = new File(absolutePath);

        if (backingFile.exists()) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(absolutePath));
            try {
                this.classProperties = (Map<String, ClassProperties>) objectInputStream.readObject();
                this.size = objectInputStream.readInt();
                int coverageCount = objectInputStream.readInt();
                this.numProbePoints = objectInputStream.readLong();

                for (int i = 0; i < coverageCount; i++) {
                    String testIdentifier = (String) objectInputStream.readObject();

                    BitSet coverageData = new BitSet(0);
                    coverageData.readExternal(objectInputStream);
                    this.testCoverages.put(testIdentifier, coverageData);

                    Long executionTime = objectInputStream.readLong();
                    this.testExecutionTimes.put(testIdentifier, executionTime);

                    updateMaxProbePointCount(coverageData);
                }

            } catch (IOException e) {
                // some problem parsing the repository. Delete it and continue initalization.
                backingFile.delete();
            } finally {
                objectInputStream.close();
            }
        }

        //System.out.println("Loaded coverage data");
        for (Map.Entry<String, BitSet> entry : testCoverages.entrySet()) {
//            System.out.printf("%s [", entry.getKey());
            for (int i = 0; i < entry.getValue().size(); i++) {
//                System.out.print(entry.getValue().get(i) ? "X" : " ");
            }
//            System.out.println("]");

            if (entry.getValue().cardinality() > 0) {
                this.empty = false;
            }
        }
    }

    public void addCoverage(String testIdentifier, Long executionTime, ClassCoverage... classCoverages) {

        synchronized (classProperties) {
            for (ClassCoverage classCoverage : classCoverages) {
                // Update the mapping of classes to start/end points in the bitset
                if (!classProperties.containsKey(classCoverage.getClassIdentifier())) {
                    final int end = size + classCoverage.getProbePointCount();
                    classProperties.put(classCoverage.getClassIdentifier(), new ClassProperties(size, end));
                    size = end;
                }
            }

            BitSet bitSet = new BitSet(size);
            for (ClassCoverage classCoverage : classCoverages) {

                // Populate the bitset at the relevant positions for this particular class
                final ClassProperties thisClassProperties = classProperties.get(classCoverage.getClassIdentifier());
                for (int i = thisClassProperties.start, j = 0; i < thisClassProperties.end; i++, j++) {
                    bitSet.set(i, classCoverage.getProbePoints().get(j));
                }
            }
            testCoverages.put(testIdentifier, bitSet);
            testExecutionTimes.put(testIdentifier, executionTime);

            // Update our measure of how many probe points there are (i.e. how many points would be 100% coverage)
            updateMaxProbePointCount(bitSet);
        }
    }

    private void updateMaxProbePointCount(BitSet bitSet) {
        final IntIterator iterator = bitSet.intIterator();
        while(iterator.hasNext()) {
            this.numProbePoints = max(this.numProbePoints, iterator.next());
        }
    }

    public TestWithCoverage getCoverage(String testIdentifier) {

        // Create a new bitset of the full size so that all outputs are of the same size no matter
        //  which order tests were added in
        BitSet result = new BitSet(size);
        final BitSet probePoints = testCoverages.get(testIdentifier);

        if (probePoints != null && !probePoints.empty()) {
            result.or(probePoints);
        }

        return new TestWithCoverage(testIdentifier, result, (long) size, testExecutionTimes.get(testIdentifier));
    }

    public void save() throws IOException {
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(backingFile));

        try {
            objectOutputStream.writeObject(this.classProperties);
            objectOutputStream.writeInt(this.size);
            objectOutputStream.writeInt(this.testCoverages.size());
            objectOutputStream.writeLong(this.size);

            for (Map.Entry<String, BitSet> entry : this.testCoverages.entrySet()) {
                objectOutputStream.writeObject(entry.getKey());
                entry.getValue().writeExternal(objectOutputStream);
                objectOutputStream.writeLong(this.testExecutionTimes.get(entry.getKey()));
            }

        } finally {
            objectOutputStream.close();
        }
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public long getNumProbePoints() {
        return size;
    }

    private static class ClassProperties implements Serializable {
        private static final long serialVersionUID = -8845823331583701999L;
        private final int start;
        private final int end;

        public ClassProperties(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
