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

echo "Stopping tests on project ($PROJECT)"

function stopTests() {
    oc delete dc chrome --loglevel=9 ${SVC_ACCOUNT_CLAUSE}
    oc delete dc robot-framework --loglevel=9 ${SVC_ACCOUNT_CLAUSE}
    oc delete svc chrome --loglevel=9 ${SVC_ACCOUNT_CLAUSE}
}

stopTests
