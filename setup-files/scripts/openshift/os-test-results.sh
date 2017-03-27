#!/bin/bash
set -e

PROJECT=$1

echo "Getting test results from the ${PROJECT} Openshift environment"

function getResults() {
    oc rsync $(oc get pods | grep robot-framework | awk '{ print $1 }'):/robot-tests/target/ robot-tests/target/
}

getResults
