package org.testpackage;

import com.google.common.base.Predicate;

/**
 * JUnit test filter which selects a subset of tests to be run on a particular test execution shard.
 * <p/>
 * Works by simply applying a modulus function to remove unneeded tests for this shard.
 * Shards only know their ID and the number of shards - there is no connectivity or sharing of data with other shards.
 *
 * @author richardnorth
 */
public class ShardingFilter implements Predicate<Class<?>> {
    private final int thisShardIndex;
    private final int numberOfShards;

    int i = 0;

    public ShardingFilter(int thisShardIndex, int numberOfShards) {
        this.thisShardIndex = thisShardIndex;
        this.numberOfShards = numberOfShards;
    }

    @Override
    public boolean apply(Class<?> input) {
        if (((i++) + thisShardIndex) % numberOfShards == 0) {
            return true;
        } else {
            return false;
        }
    }
}
