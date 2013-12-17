/*
 * Copyright 2013 Deloitte Digital and Richard North
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.deloittedigital.testpackage;

import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.jcabi.manifests.Manifests;
import com.twitter.common.testing.runner.AntJunitXmlReportListener;
import com.twitter.common.testing.runner.StreamSource;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static com.deloittedigital.testpackage.AnsiSupport.ansiPrintf;
import static com.deloittedigital.testpackage.AnsiSupport.initialize;

/**
 * @author rnorth
 */
public class TestPackage {

    public static void main(String[] args) throws IOException {

        initialize();

        String testPackage = getTestPackage();

        Set<Class<?>> testClasses = Sets.newHashSet();
        ClassPath classpath = ClassPath.from(TestPackage.class.getClassLoader());
        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(testPackage)) {
            testClasses.add(classInfo.load());
        }

        JUnitCore core = new JUnitCore();
        RunListener antXmlRunListener = new AntJunitXmlReportListener(new File("target"), new StreamSource() {
            @Override
            public byte[] readOut(Class<?> testClass) throws IOException {
                return new byte[0];
            }

            @Override
            public byte[] readErr(Class<?> testClass) throws IOException {
                return new byte[0];
            }
        });
        RunListener colouredOutputRunListener = new ColouredOutputRunListener();
        core.addListener(antXmlRunListener);
        core.addListener(colouredOutputRunListener);

        Request request = Request.classes(testClasses.toArray(new Class[testClasses.size()]));
        Result result = core.run(request);

        int failureCount = result.getFailureCount();
        int testCount = result.getRunCount();
        int passed = testCount - failureCount;

        if (failureCount > 0 || passed == 0) {
            ansiPrintf("@|red FAILED|@");
            System.exit(1);
        } else {
            ansiPrintf("@|green OK|@");
            System.exit(0);
        }
    }

    private static String getTestPackage() {
        return Manifests.read("TestPackage-Package");
    }

}
