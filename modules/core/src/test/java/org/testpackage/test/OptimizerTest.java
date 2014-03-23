package org.testpackage.test;

import org.junit.Before;
import org.junit.Test;
import org.testpackage.optimization.GreedyApproximateTestSubsetOptimizer;
import org.testpackage.optimization.TestSubsetOptimizerResult;
import org.testpackage.optimization.TestWithCoverage;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.abs;

/**
 * @author richardnorth
 */
public class OptimizerTest {

    private static final int TEST_COUNT = 100;
    private static final int COVERED_LINES = 1000000;
    private List<TestWithCoverage> coverageSets = newArrayList();
    private long startTime;

    @Before
    public void setup() {

        startTime();

        for (int test = 0; test < TEST_COUNT; test++) {

            final BitSet bitSet = new BitSet(COVERED_LINES);

            final Random random = new Random(test);

            // Simulate coverage as mostly contiguous runs of on/off to simulate methods
            boolean nextState = false;
            for (int coveredLine = 0; coveredLine < COVERED_LINES; coveredLine++) {
                if (random.nextFloat() < 0.1) {
                    nextState = !nextState;
                }
                if (random.nextFloat() < 0.8) {
                    nextState = false;
                }
                bitSet.set(coveredLine, nextState);

                // some noise to simulate branches in methods
                if (random.nextFloat() < 0.05) {
                    bitSet.flip(coveredLine);
                }
            }

            // Simulate some costs clustered around a centre that assumes each covered line costs 1, with noise
            int cost = (int) abs((bitSet.cardinality() + COVERED_LINES / 2 * random.nextGaussian()));

            coverageSets.add(new TestWithCoverage("test" + test, bitSet, cost));
        }

        stopTime("Setup");
    }

    @Test
    public void simpleTest() {

        startTime();

        final int targetTestCount = TEST_COUNT / 5;
        TestSubsetOptimizerResult result = new GreedyApproximateTestSubsetOptimizer().solve(coverageSets, targetTestCount);

        System.out.printf("The best coverage (%g%%) was achieved with tests %s\n",
                100 * ((double) result.getCoveredLines()) / COVERED_LINES,
                result.getSelections());

        stopTime("Picking " + targetTestCount + " tests");
    }


    /*
     * Simple timing functions
     */
    private void startTime() {
        startTime =  System.nanoTime();
    }

    private void stopTime(String what) {
        System.out.println(what + " took: " + (System.nanoTime() - startTime) / 1000000 + "ms");
    }
}
