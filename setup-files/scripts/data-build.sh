#!/usr/bin/env bash

set -e

cd ../../ifs-data-service
./gradlew -s build -x test -x deployToTomcat
