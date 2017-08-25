#!/usr/bin/env bash

# performs a test run against a local Openshift cluster, with no deviation in test timings for ease of side-by-side comparisons
./_environment_run.sh -l -q -e "openshift-local-performance" -p "openshift-local.properties"
