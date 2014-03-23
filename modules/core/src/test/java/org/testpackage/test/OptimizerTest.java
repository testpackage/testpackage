package org.testpackage.test;

import org.junit.BeforeClass;
import org.junit.Test;
import org.testpackage.optimization.GreedyApproximateTestSubsetOptimizer;
import org.testpackage.optimization.TestSubsetOptimizerResult;
import org.testpackage.optimization.TestWithCoverage;

import java.util.BitSet;
import java.util.Random;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.abs;

/**
 * @author richardnorth
 */
public class OptimizerTest {

    private static final int TEST_COUNT = 100;
    private static final int COVERED_LINES = 1000000;
    private static final Set<TestWithCoverage> COVERAGE_SETS = newHashSet();
    private static long startTime;

    @BeforeClass
    public static void setup() {

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

            COVERAGE_SETS.add(new TestWithCoverage("test" + test, bitSet, cost));
        }

        stopTime("Setup");
    }

    @Test
    public void testDesiredNumberOfTests() {

        startTime();

        final int targetTestCount = TEST_COUNT / 5;
        TestSubsetOptimizerResult result = new GreedyApproximateTestSubsetOptimizer()
                                                    .withTargetTestCount(targetTestCount)
                                                    .solve(COVERAGE_SETS);

        System.out.printf("The best coverage for %d tests was %g%%, and was achieved with tests %s\n",
                result.getSelections().size(),
                100 * ((double) result.getCoveredLines()) / COVERED_LINES,
                result.getSelections());

        stopTime("Picking " + targetTestCount + " tests");
    }

    @Test
    public void testDesiredCoverage() {

        startTime();

        final double targetCoverage = 0.9;
        TestSubsetOptimizerResult result = new GreedyApproximateTestSubsetOptimizer()
                                                    .withTargetTestCoverage(targetCoverage)
                                                    .solve(COVERAGE_SETS);

        System.out.printf("The desired coverage (%g%%) was achieved with %d tests %s\n",
                100 * ((double) result.getCoveredLines()) / COVERED_LINES,
                result.getSelections().size(),
                result.getSelections());

        stopTime("Picking tests with " + targetCoverage + " coverage");
    }

    @Test
    public void testDesiredCost() {

        startTime();

        final double targetCoverage = 0.9;
        final int targetCost = 700000;
        TestSubsetOptimizerResult result = new GreedyApproximateTestSubsetOptimizer()
                                                    .withTargetCost(targetCost)
                                                    .solve(COVERAGE_SETS);

        System.out.printf("The best coverage (%g%%) with target cost %d was achieved with %d tests %s\n",
                100 * ((double) result.getCoveredLines()) / COVERED_LINES,
                targetCost,
                result.getSelections().size(),
                result.getSelections());

        stopTime("Picking tests with " + targetCoverage + " coverage");
    }


    /*
     * Simple timing functions
     */
    private static void startTime() {
        startTime =  System.nanoTime();
    }

    private static void stopTime(String what) {
        System.out.println(what + " took: " + (System.nanoTime() - startTime) / 1000000 + "ms");
        System.out.flush();
    }
}
