#!/usr/bin/env bash

source bin/version.sh

# Starting the build and deploy
# if we have same name job running, current job is updated by dataflow
./gradlew clean shadowJar
java -jar build/libs/simple-streaming-$VERSION-all.jar -e prod