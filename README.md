# TestPackage

TestPackage is a simple Java library designed to make it easy to bundle JUnit tests in standalone executable JAR form, rather than the traditional model of running them from an Ant/Maven/Gradle build script. 

It is **not intended for running unit tests**, which clearly need to be built around the units of code under test (on the same classpath). However, for situations like integration tests, functional tests and smoke tests, inclusion in a build script adds unnecessary complexity and makes some opportunities harder to grasp.

Additionally, TestPackage aims to provide more useful and intuitive output than other JUnit runners give, for example making it easier to spot assertion errors and root causes of exceptions at a glance.

![Quick demo](docs/demo1.gif)

## Why make tests standalone?

With integration, functional and smoke tests, for example, the following should become possible with standalone test bundles:

 * Running tests within/from environments or systems that do not have development tools installed
 * Making it easier to run post-deployment smoke tests from a configuration management/deployment tool - the same smoke tests can be used in all stages of the release pipeline, without the need for a release to be accompanied by a heavy tail of source code and build-time dependencies
 * Enabling non-technical staff to run tests in their local environment, as an executable they can double click (perhaps with a GUI displaying results)
 * Clearer versioning of tests alongside releasable artifacts - less indirection
 * Just as application binaries should be build-once-run-many to reduce risk of build-time dependency variation, the same principle can and should be applied to tests where possible.
 * _Slight_ reduction in the time overhead of running tests, since they do not need to be recompiled for each run (if the tests themselves aren't being changed).

## Features

 * Supports running JUnit tests standalone from the command line (although as standard unadorned JUnit tests they can obviously still be run with other tools, IDEs, etc).
 * Provides `VisibleAssertions` replacement for `org.junit.Assert` which makes it easier to identify causes of assertion errors
 * Pretty, clean output of test failures, focusing on information that is most useful to the developer without displaying huge stack traces
 * JUnit XML report output, compatible with Jenkins (uses code from Twitter Commons, see attributions below)
 * Fail-fast mode, which aborts the test run when the first test failure happens
 * Test prioritisation, which runs recently failed tests first, on the basis that these are most likely to identify persistent problems. This can be particularly powerful when used with fail-fast mode.

## How do I use it?

`examples/maven` is a simple Maven project which uses TestPackage. The following elements are core prerequisites for using TestPackage:

**TestPackage library dependency**

	    <dependency>
            <groupId>org.testpackage.</groupId>
            <artifactId>testpackage</artifactId>
        </dependency>

**Maven Shade plugin to compile test sources, dependencies and TestPackage into a single executable JAR**

_Note that **TestPackage-Package** must be set to the package where test classes reside. Other configuration remains as-is._

		<plugin>
		    <groupId>org.apache.maven.plugins</groupId>
		    <artifactId>maven-shade-plugin</artifactId>
		    <executions>
		        <execution>
		            <phase>package</phase>
		            <goals>
		                <goal>shade</goal>
		            </goals>
		            <configuration>
		                <transformers>
		                    <transformer
		                            implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
		                        <manifestEntries>
		                            <Main-Class>
		                            	org.testpackage.TestPackage
		                           	 </Main-Class>
		                            <TestPackage-Package>
		                            	org.testpackage.example.maven
		                            </TestPackage-Package>
		                        </manifestEntries>
		                    </transformer>
		                </transformers>
		            </configuration>
		        </execution>
		    </executions>
		</plugin>

**Configuration of integration test source location (not needed if you don't mind keeping integration test sources under src/main/java)**

	<plugin>
         <groupId>org.codehaus.mojo</groupId>
         <artifactId>build-helper-maven-plugin</artifactId>
         <executions>
             <execution>
                 <id>add-test-source</id>
                 <phase>generate-sources</phase>
                 <goals>
                     <goal>add-source</goal>
                 </goals>
                 <configuration>
                     <sources>
                         <source>${basedir}/src/int-test/java</source>
                     </sources>
                 </configuration>
             </execution>
         </executions>
     </plugin>

### Building standalone test JAR with Maven

With the configuration outlined above, simply run `mvn clean package` to produce an executable JAR.

### Running the tests

Once built, execute the build JAR file (e.g. named `maven-example-1.0-SNAPSHOT.jar`) by running:

	java -jar target/maven-example-1.0-SNAPSHOT.jar

Of course, this JAR file can be moved anywhere on the filesystem, deployed to an artifact repository etc (e.g. with `mvn deploy`).

While the above runs with sensible defaults, the following arguments may be passed at the command line to customise behaviour:

#####Options

    --failfast or -ff:      Enables fail-fast mode

#####Arguments

The full package names which should be searched (non-recursively) for test classes

#####Usage
    java -jar JARFILE [OPTIONS] [ARGUMENTS]

# TODO and issues

See the [issue tracker](https://github.com/testpackage/testpackage/issues) on Github.

# Contributing

**Please follow the Fork and Pull model for contributing changes (see [here](https://confluence.atlassian.com/display/BITBUCKET/Fork+a+Repo,+Compare+Code,+and+Create+a+Pull+Request) for a detailed explanation).**

While the example project given is a Maven project, TestPackage itself is built with Gradle. Use the Gradle build wrapper and build as follows:

	./gradlew build

To run tests:

    ./gradlew test --info

*Please note that when running tests there will be 'failures' and assertion errors in stdout. This is because the unit tests have to test that output looks correct when failures occur. The overall result of the unit tests should still be a pass, though.*

To install the built TestPackage library to your local (~/.m2) Maven repository run:

	./gradlew install

# Licence and Attributions

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Copyright, the authors (listed in AUTHORS)

The current version of the library includes a modified excerpt of code from Twitter Commons, used under its Apache 2 licence.