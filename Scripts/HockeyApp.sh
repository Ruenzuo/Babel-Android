#!/bin/sh

# Executing Gradle wrapper
./gradlew clean build

# Creating build number
DIR='./'
count=`git rev-list HEAD | wc -l | sed -e 's/ *//g' | xargs -n1 printf %04d`
commit=`git show --abbrev-commit HEAD | grep '^commit' | sed -e 's/commit //'`
buildno=b"$count.$commit"
# Distributing IPA over HockeyApp
puck -submit=auto -download=true -notes="Build by Jenkins CI with build number $buildno" -api_token=5d8cfd54f51e4428986c8f3c34f77bb1 -app_id=3e0cec23c4ee5f70899c8723fa21e5c8 "app/build/outputs/apk/app-release.apk"