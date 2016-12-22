#!/bin/bash

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

cd ../../../
./gradlew -Pprofile=docker flywayClean flywayMigrate
cd $BASEDIR
./syncShib.sh
