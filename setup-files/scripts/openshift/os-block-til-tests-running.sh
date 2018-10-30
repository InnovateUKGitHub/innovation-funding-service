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

PODNAME=$(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep ^robot-framework | grep -v deploy | awk '{ print $1 }')

echo "Waiting tests to be ready on $PODNAME"

until oc logs $PODNAME ${SVC_ACCOUNT_CLAUSE} | grep "/robot-tests/target/.*/report.html"; do
    echo "Tests are not done yet.."
    sleep 60
done
