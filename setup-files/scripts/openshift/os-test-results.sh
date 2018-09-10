#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

. $(dirname $0)/deploy-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
HOST=$(getHost $TARGET)
ROUTE_DOMAIN=$(getRouteDomain $TARGET $HOST)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)

echo "Getting test results from the current oc project (${PROJECT})"

function getResults() {
    rm -rf robot-tests/test-results-${PROJECT}
    mkdir -p robot-tests/test-results-${PROJECT}
    oc ${SVC_ACCOUNT_CLAUSE} rsync $(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep robot-framework | awk '{ print $1 }'):/robot-tests/target/ robot-tests/test-results-${PROJECT}
}

getResults
