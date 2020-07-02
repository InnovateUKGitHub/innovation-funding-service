#!/usr/bin/env bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3
NEXUS_USER=$4
NEXUS_PASS=$5
NEXUS_EMAIL=$6
NEXUS_VERSION=$7

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
HOST=$(getHost $TARGET)
ROUTE_DOMAIN=$(getRouteDomain $TARGET $HOST)
REGISTRY=$(getRegistry)
NEXUS_REGISTRY=$(getNexusRegistryUrl)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)
REGISTRY_TOKEN=$SVC_ACCOUNT_TOKEN

echo "Deploying the $PROJECT Openshift project"

function deploy() {

    if ! $(isNamedEnvironment ${TARGET}); then
        #oc create -f $(getBuildLocation)/shib/55-ldap.yml ${SVC_ACCOUNT_CLAUSE}
        #oc create -f $(getBuildLocation)/mail/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mysql/3-mysql.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mysql/3-finance-totals-mysql.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mysql/survey-mysql.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/redis/cache-provider.yml ${SVC_ACCOUNT_CLAUSE}
    fi

    # The SIL stub is required in all environments, in one form or another, except for production
    #if ! $(isProductionEnvironment ${TARGET}); then
     #   oc create -f $(getBuildLocation)/sil-stub/ ${SVC_ACCOUNT_CLAUSE}
    #fi

    #oc create -f $(getBuildLocation)/ifs-services/ ${SVC_ACCOUNT_CLAUSE}
    #oc create -f $(getBuildLocation)/survey/ ${SVC_ACCOUNT_CLAUSE}

    #oc create -f $(getBuildLocation)/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
    #oc create -f $(getBuildLocation)/shib/56-idp.yml ${SVC_ACCOUNT_CLAUSE}
}

function addAbilityToPullFromNexus() {
    oc secrets new-dockercfg ifs-external-registry \
        --docker-username=${NEXUS_USER} \
        --docker-password=${NEXUS_PASS} \
        --docker-email=${NEXUS_EMAIL} \
        --docker-server=${NEXUS_REGISTRY}

    oc secrets add serviceaccount/builder secrets/ifs-external-registry ${SVC_ACCOUNT_CLAUSE}
}

# Entry point
tailorAppInstance

if [[ ${TARGET} == "local" ]]
then
    replacePersistentFileClaim
fi

addAbilityToPullFromNexus
# use hardcoded version as our version is one above the release so does not exist
useBaseNexusRegistry
deploy
blockUntilServiceIsUp

if [[ ${TARGET} == "local" || ${TARGET} == "remote" ]]
then
    shibInit
fi
