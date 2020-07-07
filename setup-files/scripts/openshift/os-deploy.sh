#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3
IMAGE_REGISTRY=$4
NEXUS_USER=$5
NEXUS_PASS=$6
NEXUS_EMAIL=$7
NEXUS_VERSION=$8

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

REGISTRY=$(getRegistry)
INTERNAL_REGISTRY=$(getInternalRegistry)
NEXUS_REGISTRY=$(getNexusRegistryUrl)

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
HOST=$(getHost $TARGET)
ROUTE_DOMAIN=$(getRouteDomain $TARGET $HOST)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)
REGISTRY_TOKEN=$SVC_ACCOUNT_TOKEN

echo "Deploying the $PROJECT Openshift project"

function deploy() {

    if ! $(isNamedEnvironment ${TARGET}); then
        oc create -f $(getBuildLocation)/shib/55-ldap.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mail/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mysql/3-mysql.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mysql/3-finance-totals-mysql.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mysql/survey-mysql.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/redis/cache-provider.yml ${SVC_ACCOUNT_CLAUSE}
    fi

    # The SIL stub is required in all environments, in one form or another, except for production
    if ! $(isProductionEnvironment ${TARGET}); then
        oc create -f $(getBuildLocation)/sil-stub/ ${SVC_ACCOUNT_CLAUSE}
    fi

    oc create -f $(getBuildLocation)/ifs-services/ ${SVC_ACCOUNT_CLAUSE}
    oc create -f $(getBuildLocation)/survey/ ${SVC_ACCOUNT_CLAUSE}

    oc create -f $(getBuildLocation)/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
    oc create -f $(getBuildLocation)/shib/56-idp.yml ${SVC_ACCOUNT_CLAUSE}
}

# Entry point
tailorAppInstance

if [[ ${TARGET} == "local" ]]
then
    replacePersistentFileClaim
fi


if [[ ${IMAGE_REGISTRY} == "nexus" ]]
then
    addAbilityToPullFromNexus
    # use hardcoded version as our version is one above the release so does not exist
    useNexusRegistry ${NEXUS_VERSION}
else
    useContainerRegistry
fi

deploy
blockUntilServiceIsUp

if [[ ${TARGET} == "local" || ${TARGET} == "remote" ]]
then
    shibInit
fi
