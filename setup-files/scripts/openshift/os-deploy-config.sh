#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

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

echo "Applying the config maps for $PROJECT Openshift project"

function applyConfigMaps {
    oc apply -f $(getBuildLocation)/config-maps/acc-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/cache-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/data-service-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/db-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/finance-db-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/flyway-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/grant-db-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/ldap-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/new-relic-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/performance-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/shibboleth-config.yml.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/survey-db-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/web-config.yml ${SVC_ACCOUNT_CLAUSE}
}

# Entry point
applyConfigMaps

