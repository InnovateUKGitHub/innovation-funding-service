#!/bin/bash
set -e


PROJECT=$(oc project -q)

echo "Getting test results from the current oc project (${PROJECT})"

function getResults() {
    # rm -rf robot-tests/test-results-${PROJECT}
    mkdir -p robot-tests/test-results-${PROJECT}
    oc rsync $(oc get pods | grep robot-framework | awk '{ print $1 }'):/robot-tests/target/ robot-tests/test-results-${PROJECT}
}

getResults
