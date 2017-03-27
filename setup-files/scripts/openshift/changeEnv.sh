#!/usr/bin/env bash
sed -i '/openshiftEnv/c\' ~/.gradle/gradle.properties && echo "openshiftEnv=$1" >> ~/.gradle/gradle.properties