#!/bin/bash

BASEDIR=$(dirname "$0")
cd $BASEDIR

eval $(docker-machine env default)
cd ../../../
./gradlew -Pprofile=docker flywayClean flywayMigrate
cd $BASEDIR
./syncShib.sh