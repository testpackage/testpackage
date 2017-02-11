#!/bin/bash

set -x
set -euo pipefail
pushd server
mvn package

# Run a standalone web app server
java "-javaagent:$(pwd)/ext/jacocoagent.jar=output=tcpserver" -jar target/server-1.0-SNAPSHOT.jar &
SERVER_PID=$!
sleep 10

popd

pushd tests
# Build TestPackage test JAR
mvn package

# Clear coverage data
rm -rf .testpackage

set +x
exec 5>&1
# Run TestPackage test JAR
echo "@@@ FIRST_RUN"
RUN=$(java -jar target/tests-1.0-SNAPSHOT.jar |tee /dev/fd/5)
echo ${RUN} | grep -q "3 passed"
echo ${RUN} | grep -q "Guessing that coverage for classes under 'org.testpackage' should be recorded"
echo ${RUN} | grep -q "Connected to JaCoCo agent at localhost:6300"

# Run TestPackage test JAR again with coverage data
echo "@@@ SECOND_RUN"
RUN=$(java -jar target/tests-1.0-SNAPSHOT.jar --optimize-coverage=100% |tee /dev/fd/5)
echo ${RUN} | grep -q "3 passed"
echo ${RUN} | grep -q "Optimizer complete - plan is 3 tests with 76.9% coverage"

# Run TestPackage test JAR again with coverage data and an optimization goal
echo "@@@ THIRD_RUN_EXPECT_1"
RUN=$(java -jar target/tests-1.0-SNAPSHOT.jar --optimize-coverage=53% |tee /dev/fd/5)
echo ${RUN} | grep -q "1 passed"
echo ${RUN} | grep -q "Optimizer complete - plan is 1 tests with 53.8% coverage"

# Run TestPackage test JAR again with coverage data and an optimization goal
echo "@@@ FOURTH_RUN_EXPECT_2"
RUN=$(java -jar target/tests-1.0-SNAPSHOT.jar --optimize-coverage=54% |tee /dev/fd/5)
echo ${RUN} | grep -q "2 passed"
echo ${RUN} | grep -q "Optimizer complete - plan is 2 tests with 61.5% coverage"

# Run TestPackage test JAR again with coverage data and an optimization goal
echo "@@@ FIFTH_RUN_EXPECT_3"
RUN=$(java -jar target/tests-1.0-SNAPSHOT.jar --optimize-coverage=62% |tee /dev/fd/5)
echo ${RUN} | grep -q "3 passed"
echo ${RUN} | grep -q "Optimizer complete - plan is 3 tests with 69.2% coverage"

# kill server
kill $SERVER_PID
