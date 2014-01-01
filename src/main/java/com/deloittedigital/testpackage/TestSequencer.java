package com.deloittedigital.testpackage;

import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import org.junit.runner.Request;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by richardnorth on 20/12/2013.
 */
public class TestSequencer {

    public Request sequenceTests(String... testPackageNames) throws IOException {
        return sequenceTests(Collections.<String, Integer>emptyMap(), testPackageNames);
    }

    public Request sequenceTests(Map<String, Integer> runsSinceLastFailures, String... testPackageNames) throws IOException {
        Set<Class<?>> testClasses = Sets.newHashSet();

        for (String testPackageName : testPackageNames) {
            ClassPath classpath = ClassPath.from(TestPackage.class.getClassLoader());
            for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(testPackageName)) {
                testClasses.add(classInfo.load());
            }
        }
        Request unsortedClassRequest = Request.classes(testClasses.toArray(new Class[testClasses.size()]));
        Request sortedRequest = unsortedClassRequest.sortWith(new RecentFailurePrioritisationRequestComparator(runsSinceLastFailures));

        return sortedRequest;
    }

}
