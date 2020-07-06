#!/usr/bin/env bash

set -e

NEXUS_USER=$4
NEXUS_PASS=$5
NEXUS_EMAIL=$6
NEXUS_VERSION=$7

. $(dirname $0)/os-deploy-services.sh
. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

NEXUS_REGISTRY=$(getNexusRegistryUrl)

echo "Deploying the $PROJECT Openshift project using nexus images"

# Entry point
tailorAppInstance

if [[ ${TARGET} == "local" ]]
then
    replacePersistentFileClaim
fi

addAbilityToPullFromNexus
# use hardcoded version as our version is one above the release so does not exist
useNexusRegistry ${NEXUS_VERSION}
deploy
blockUntilServiceIsUp

if [[ ${TARGET} == "local" || ${TARGET} == "remote" ]]
then
    shibInit
fi
