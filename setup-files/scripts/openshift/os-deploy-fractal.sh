#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

if [[ "$TARGET" == "production" ]]; then
    echo "Cannot deploy fractal to production"
    exit 1
fi

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
HOST=$(getHost $TARGET)
ROUTE_DOMAIN=$(getRouteDomain $TARGET $HOST)
REGISTRY=$(getRegistry)
INTERNAL_REGISTRY=$(getInternalRegistry)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)
REGISTRY_TOKEN=$SVC_ACCOUNT_TOKEN

echo "Deploying Fractal for the $PROJECT Openshift project"

function deployFractal() {
    until oc create -f $(getBuildLocation)/fractal/9-fractal.yml ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete -f $(getBuildLocation)/fractal/9-fractal.yml ${SVC_ACCOUNT_CLAUSE}
      sleep 10
    done

}

# Entry point

    tailorFractalInstance
    useContainerRegistry
    pushFractalImages
    deployFractal
    blockUntilServiceIsUp


