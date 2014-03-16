package org.testpackage;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import java.util.List;

/**
 * JUnit test filter which selects a subset of tests to be run on a particular test execution shard.
 *
 * Works by simply applying a modulus function to remove unneeded tests for this shard.
 * Shards only know their ID and the number of shards - there is no connectivity or sharing of data with other shards.
 *
 * @author richardnorth
 */
public class ShardingFilter extends Filter {
    private final int thisShardIndex;
    private final int numberOfShards;
    private final List<Class<?>> testClassNames;

    public ShardingFilter(int thisShardIndex, int numberOfShards, List<Class<?>> testClassNames) {
        this.thisShardIndex = thisShardIndex;
        this.numberOfShards = numberOfShards;
        this.testClassNames = testClassNames;
    }

    @Override
    public boolean shouldRun(Description description) {
        if (description.isTest()) {
            return true;
        }

        if (((testClassNames.indexOf(description.getTestClass()) + thisShardIndex) % numberOfShards) == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String describe() {
        return String.format("Shard %d (0..%d)", thisShardIndex, numberOfShards - 1);
    }
}
