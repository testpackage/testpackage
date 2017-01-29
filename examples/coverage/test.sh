#!/bin/bash

cd server
mvn clean package

# Run a standalone web app server
java "-javaagent:$(pwd)/ext/jacocoagent.jar=output=tcpserver" -jar target/server-1.0-SNAPSHOT.jar &
SERVER_PID=$!
sleep 30

cd ../tests
# Build TestPackage test JAR
mvn clean package

# Clear coverage data
rm -rf .testpackage

# Run TestPackage test JAR
echo "@@@ FIRST_RUN"
java -jar target/tests-1.0-SNAPSHOT.jar

# Run TestPackage test JAR again with coverage data
echo "@@@ SECOND_RUN"
java -jar target/tests-1.0-SNAPSHOT.jar --optimize-coverage=100%

# Run TestPackage test JAR again with coverage data and an optimization goal
echo "@@@ THIRD_RUN_EXPECT_1"
java -jar target/tests-1.0-SNAPSHOT.jar --optimize-coverage=53%

# Run TestPackage test JAR again with coverage data and an optimization goal
echo "@@@ FOURTH_RUN_EXPECT_2"
java -jar target/tests-1.0-SNAPSHOT.jar --optimize-coverage=54%

# Run TestPackage test JAR again with coverage data and an optimization goal
echo "@@@ FIFTH_RUN_EXPECT_3"
java -jar target/tests-1.0-SNAPSHOT.jar --optimize-coverage=62%

# kill server
kill $SERVER_PID
