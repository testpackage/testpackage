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

import com.deloittedigital.testpackage.sequencing.TestHistoryRepository;
import com.deloittedigital.testpackage.sequencing.TestHistoryRunListener;
import com.twitter.common.testing.runner.AntJunitXmlReportListener;
import com.twitter.common.testing.runner.StreamSource;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import static com.deloittedigital.testpackage.AnsiSupport.ansiPrintf;
import static com.deloittedigital.testpackage.AnsiSupport.initialize;

/**
 * @author rnorth
 */
public class TestPackage {

    protected TestSequencer testSequencer = new TestSequencer();

    public static void main(String[] args) throws IOException {

        initialize();

        int exitCode = new TestPackage().run();

        System.exit(exitCode);
    }

    public int run() throws IOException {

        new File(".testpackage").mkdir();
        TestHistoryRepository testHistoryRepository = new TestHistoryRepository(".testpackage/history.txt");
        TestHistoryRunListener testHistoryRunListener = new TestHistoryRunListener(testHistoryRepository);

        String testPackage = getTestPackage();

        Request request = testSequencer.sequenceTests(testHistoryRepository.getRunsSinceLastFailures(), testPackage);

        JUnitCore core = new JUnitCore();

        File targetDir = new File("target");
        boolean mkdirs = targetDir.mkdirs();
        if (!(targetDir.exists() || mkdirs)) {
            throw new TestPackageException("Could not create target directory: " + targetDir.getAbsolutePath());
        }
        RunListener antXmlRunListener = new AntJunitXmlReportListener(targetDir, new StreamSource() {
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
        core.addListener(testHistoryRunListener);

        Result result = core.run(request);

        int failureCount = result.getFailureCount();
        int testCount = result.getRunCount();
        int passed = testCount - failureCount;

        testHistoryRepository.save();

        if (failureCount > 0 || passed == 0) {
            ansiPrintf("@|red FAILED|@");
            return 1;
        } else {
            ansiPrintf("@|green OK|@");
            return 0;
        }
    }


    private static String getTestPackage() {

        try {
            Enumeration<URL> resources = TestPackage.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                Manifest manifest = new Manifest(url.openStream());
                String attributes = manifest.getMainAttributes().getValue("TestPackage-Package");
                if (attributes != null) {
                    return attributes;
                }
            }

            return System.getProperty("package");
        } catch (IOException e) {
            throw new TestPackageException("Error loading MANIFEST.MF", e);
        }
    }

}
