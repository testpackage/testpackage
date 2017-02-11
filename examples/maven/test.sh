#!/bin/bash

set -x
set -euo pipefail
mvn package

# Run a standalone web app server
exec 5>&1
set +e
RUN=$(java -jar target/maven-example-1.0-SNAPSHOT.jar |tee /dev/fd/5)
set -e

set +x
echo ${RUN} | grep -qE ">>\s+BasicTest.testDeliberateAssertionFailure"
echo ${RUN} | grep -qE "\[ 0/6 tests run \]"
echo ${RUN} | grep -qE "✘\s+BasicTest.testDeliberateAssertionFailure"
echo ${RUN} | grep -qE ">>\s+BasicTest.testDeliberateError"
echo ${RUN} | grep -qE "\[ 1/6 tests run"
echo ${RUN} | grep -qE "\[ 2/6 tests run"
echo ${RUN} | grep -qE "✘\s+BasicTest.testDeliberateError"
echo ${RUN} | grep -qE ">>\s+BasicTest.testEqualsAssertion"
echo ${RUN} | grep -qE "2 failed \]"
echo ${RUN} | grep -qE "✔\s+BasicTest.testEqualsAssertion"
echo ${RUN} | grep -qE "STDOUT:\s+✘ Deliberate assertion failure"
echo ${RUN} | grep -qE "4 passed, 2 failed, 0 ignored"
echo ${RUN} | grep -qE "FAILED"
