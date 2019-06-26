#!/bin/bash
# Auto build or deploy

set -e

if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_TAG" != "" ]; then
  echo "Build for Release => Branch ["$TRAVIS_BRANCH"]  Tag ["$TRAVIS_TAG"]"
  ./gradlew -PbintrayUser="$BINTRAY_USER" -PbintrayKey="$BINTRAY_KEY" clean bintrayUpload
else
  echo "Build => Branch ["$TRAVIS_BRANCH"]  Tag ["$TRAVIS_TAG"]"
  ./gradlew connectedAndroidTest
fi