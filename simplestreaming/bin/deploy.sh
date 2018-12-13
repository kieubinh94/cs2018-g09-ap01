#!/usr/bin/env bash

USER=$(gcloud config get-value account)
USER=${USER/@gmail.com/}
USER=${USER/./}

source bin/version.sh

# Starting the build and deploy
# if we have same name job running, current job is updated by dataflow
./gradlew clean shadowJar
java -jar build/libs/simple-streaming-$VERSION-all.jar
