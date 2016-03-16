#!/usr/bin/env bash

cd ../../../../../ifs-data-service
ifsDataFolder="$(pwd)"

tempReadProperty=`sed '/^\#/d' ${ifsDataFolder}/dev-build.gradle | grep "$1"  | cut -d "=" -f2 | sed 's/"//g'`
echo ${tempReadProperty}
