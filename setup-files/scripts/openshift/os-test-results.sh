#!/bin/bash
set -e

ENV=$1
HOST=dev.ifs-test-clusters.com

echo "Getting test results from the $ENV Openshift environment"

function getResults() {
    oc rsync $(oc get pods | grep robot-framework | awk '{ print $1 }'):/robot-tests/target/ robot-tests/target/
}

getResults
