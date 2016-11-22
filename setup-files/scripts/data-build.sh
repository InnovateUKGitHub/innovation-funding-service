#!/usr/bin/env bash

set -e

cd ../../ifs-data-service
./gradlew -s cleanDeploy -x test -x deployToTomcat
