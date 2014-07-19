#!/bin/bash

cd server
mvn clean package

# Run a standalone web app server
java -javaagent:$(pwd)/ext/jacocoagent.jar=output=tcpserver -jar target/server-1.0-SNAPSHOT.jar &
SERVER_PID=$!

cd ../tests
# Build TestPackage test JAR
mvn clean package

# Run TestPackage test JAR
java -jar target/tests-1.0-SNAPSHOT.jar

# kill server
kill $SERVER_PID
