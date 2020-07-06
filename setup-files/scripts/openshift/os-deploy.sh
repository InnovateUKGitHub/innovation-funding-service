#!/bin/bash

set -e

. $(dirname $0)/os-deploy-services.sh
. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

REGISTRY=$(getRegistry)
INTERNAL_REGISTRY=$(getInternalRegistry)

echo "Deploying the $PROJECT Openshift project"

# Entry point
tailorAppInstance

if [[ ${TARGET} == "local" ]]
then
    replacePersistentFileClaim
fi

useContainerRegistry
deploy
blockUntilServiceIsUp

if [[ ${TARGET} == "local" || ${TARGET} == "remote" ]]
then
    shibInit
fi
