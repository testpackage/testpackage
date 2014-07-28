package org.testpackage;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import org.junit.runner.Request;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class TestSequencer {

    private final int thisShardIndex;
    private final int numberOfShards;

    /**
     * Create a new TestSequencer with sharding support.
     * @param thisShardIndex    the index number of this shard (zero indexed; from 0 to n-1)
     * @param numberOfShards    the total number of shards (n)
     */
    public TestSequencer(int thisShardIndex, int numberOfShards) {

        this.thisShardIndex = thisShardIndex;
        this.numberOfShards = numberOfShards;
    }

    /**
     * Create a new TestSequencer without sharding support (i.e. assume this is the only shard)
     */
    public TestSequencer() {
        this(0, 1);
    }

    public Request sequenceTests(String... testPackageNames) throws IOException {
        return sequenceTests(Collections.<String, Integer>emptyMap(), testPackageNames);
    }

    public Request sequenceTests(Map<String, Integer> runsSinceLastFailures, String... testPackageNames) throws IOException {
        List<Class<?>> testClasses = Lists.newArrayList();

        for (String testPackageName : testPackageNames) {
            ClassPath classpath = ClassPath.from(TestPackage.class.getClassLoader());
            for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(testPackageName)) {
                testClasses.add(classInfo.load());
            }
        }

        // Before filtering, sort into a consistent order
        Collections.sort(testClasses, new LexicographicRequestComparator());

        // Filter, in case of sharding
        List<Class<?>> shardFilteredTestClasses = Lists.newArrayList();
        for (int i=thisShardIndex; i < testClasses.size(); i += numberOfShards) {
            shardFilteredTestClasses.add(testClasses.get(i));
        }

        // Convert to a JUnit request
        Request unprioritised = Request.classes(shardFilteredTestClasses.toArray(new Class[shardFilteredTestClasses.size()]));

        // Re-sort according to test priority (run recently-failed tests first)
        return unprioritised.sortWith(new RecentFailurePrioritisationRequestComparator(runsSinceLastFailures));
    }

}
