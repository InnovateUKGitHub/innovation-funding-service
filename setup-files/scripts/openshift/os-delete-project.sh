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

echo "Deleting $PROJECT Openshift project"

oc delete project ${PROJECT} ${SVC_ACCOUNT_CLAUSE}

until oc get project ${PROJECT} ${SVC_ACCOUNT_CLAUSE}; do
    echo "Project is not deleted yet.."
    sleep 15
done

echo "Project ${PROJECT} is deleted."
