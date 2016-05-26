#!/bin/bash

sudo echo "Entering sudo now so we can run the tests without pause later"
./build-all.sh
wait
./run_tests_locally.sh
