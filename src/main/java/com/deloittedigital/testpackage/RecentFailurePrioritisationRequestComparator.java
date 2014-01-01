package com.deloittedigital.testpackage;

import com.google.common.collect.ImmutableMap;
import org.junit.runner.Description;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator which orders test descriptions according to a map of how long ago tests have failed.
 * JUnit uses this at both the class description level and method description level,
 * so a 'time since last failure' should be recorded for both classes and methods in the same map.
 * <p/>
 * i.e. if MyTestClass.testA() has just failed, there should be a zero entry for both MyTestClass
 * and MyTestClass.testA.
 * <p/>
 * Created by richardnorth on 01/01/2014.
 */
class RecentFailurePrioritisationRequestComparator implements Comparator<Description> {
    private final Map<String, Integer> runsSinceLastFailures;

    public RecentFailurePrioritisationRequestComparator(Map<String, Integer> runsSinceLastFailures) {
        this.runsSinceLastFailures = ImmutableMap.copyOf(runsSinceLastFailures);
    }

    @Override
    public int compare(Description o1, Description o2) {

        Integer timeSinceO1Failure = runsSinceLastFailures.get(o1.getDisplayName());
        Integer timeSinceO2Failure = runsSinceLastFailures.get(o2.getDisplayName());

        if (timeSinceO1Failure == null) {
            timeSinceO1Failure = Integer.MAX_VALUE;
        }

        if (timeSinceO2Failure == null) {
            timeSinceO2Failure = Integer.MAX_VALUE;
        }

        if (timeSinceO1Failure < timeSinceO2Failure) {
            return -1;
        } else if (timeSinceO1Failure > timeSinceO2Failure) {
            return 1;
        } else {
            // Just use lexicographic order if no recent failures or both just as recent
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }
    }
}
