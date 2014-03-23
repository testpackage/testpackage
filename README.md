# TestPackage 
## [See the project web site for background](http://testpackage.org)

[![Build Status](https://travis-ci.org/testpackage/testpackage.png?branch=master)](https://travis-ci.org/testpackage/testpackage)


TestPackage is a simple Java library designed to make it easy to bundle JUnit tests in standalone executable JAR form, rather than the traditional model of running them from an Ant/Maven/Gradle build script.


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
