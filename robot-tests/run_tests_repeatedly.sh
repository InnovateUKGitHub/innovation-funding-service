#!/bin/bash
# A simple script to run a test suite several times in a row, to aid with tracking down sporadic failures

for ((i=0;i<=$2;i++))
do 
    ./micro_run_tests.sh -o -d $1
done

