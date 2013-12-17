# TestPackage

TestPackage is a simple Java library designed to make it easy to bundle JUnit tests in standalone executable JAR form, rather than the traditional model of running them from an Ant/Maven/Gradle build script. 

It is **not intended for running unit tests**, which clearly need to be built around the units of code under test (on the same classpath). However, for situations like integration tests, functional tests and smoke tests, inclusion in a build script adds unnecessary complexity and makes some opportunities harder to grasp.

Additionally, TestPackage aims to provide more useful and intuitive output than other JUnit runners give, for example making it easier to spot assertion errors and root causes of exceptions at a glance.

![Quick demo](https://bitbucket.org/devservices/testpackage/raw/87d0cff0e573ddd80ec6009ad324a442d6661412/docs/demo1.gif)

## Why make tests standalone?

With integration, functional and smoke tests, for example, the following should become possible with standalone test bundles:

 * Running tests within/from environments or systems that do not have development tools installed
 * Making it easier to run post-deployment smoke tests from a configuration management/deployment tool - the same smoke tests can be used in all stages of the release pipeline, without the need for a release to be accompanied by a heavy tail of source code and build-time dependencies
 * Enabling non-technical staff to run tests in their local environment, as an executable they can double click (perhaps with a GUI displaying results)
 * Clearer versioning of tests alongside releasable artifacts - less indirection
 * Just as application binaries should be build-once-run-many to reduce risk of build-time dependency variation, the same principle can and should be applied to tests where possible.
 
## Features

 * Supports running JUnit tests standalone from the command line
 * Provides `VisibleAssertions` replacement for `org.junit.Assert` which makes it easier to identify causes of assertion errors
 * Pretty, clean output of test failures, focusing on information that is most useful to the developer without displaying huge stack traces
 * JUnit XML report output, compatible with Jenkins (uses code from Twitter Commons, see attributions below)

## How do I use it?

`examples/maven` is a simple Maven project which uses TestPackage. The following elements are core prerequisites for using TestPackage:

**TestPackage library dependency**

	    <dependency>
            <groupId>com.deloittedigital</groupId>
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
		                            	com.deloittedigital.testpackage.TestPackage
		                           	 </Main-Class>
		                            <TestPackage-Package>
		                            	com.deloittedigital.testpackage.example.maven
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

Of course, this JAR file can be moved anywhere on the filesystem, deployed to an artifact repository (e.g. with `mvn deploy`)

TODO: Example output

# TODO

#### Features
 
 * Complete `VisibleAssertions` to provide fuller coverage of `org.junit.Assert`
 * Test sharding, e.g. for running a fraction of tests concurrently using separate CI slaves
 * Toggleable Fail-fast mode
 * Retention of historic failure rates per test, and sorting of tests so that high-risk tests run earliest (particularly useful with fail fast mode)
 * Capture of stdout/stderr in JUnit XML output
 * Simple Swing-based UI to display logs when JAR is executed in a GUI environment rather than a terminal
 

#### General

 * Attach licence and open source
 * Release to public Maven repository
 * Add example output to this README.
 * Reduce library dependencies, e.g. remove dependency on jcabi-manifests which brings lots of transitive dependencies.

# Contributing

While the example project given is a Maven project, TestPackage itself is built with Gradle. Use the Gradle build wrapper and build as follows:

	./gradlew build

To install the built TestPackage library to your local (~/.m2) Maven repository run:

	./gradlew install

# Licence and Attributions

Apache 2 Licence

Copyright, the authors (listed in AUTHORS)

The current version of the library includes a modified excerpt of code from Twitter Commons, used under its Apache 2 licence.