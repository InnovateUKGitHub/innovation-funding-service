#!/bin/bash

set -e

PROJECT=$1
TARGET=$2

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)

echo "Applying the config maps for $PROJECT Openshift project"

function applyConfigMaps {
    oc apply -f $(getBuildLocation)/config-maps/cache-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/data-service-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/db-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/finance-db-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/flyway-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/ldap-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/idp-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/new-relic-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/performance-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/shibboleth-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/survey-db-config.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/config-maps/web-config.yml ${SVC_ACCOUNT_CLAUSE}
}

function applyRoutes {
    oc apply -f $(getBuildLocation)/routes/idp-route.yml ${SVC_ACCOUNT_CLAUSE}
    oc apply -f $(getBuildLocation)/routes/shib-route.yml ${SVC_ACCOUNT_CLAUSE}

    if ! $(isNamedEnvironment ${TARGET}); then
        oc apply -f $(getBuildLocation)/routes/mail-route.yml ${SVC_ACCOUNT_CLAUSE}
    fi
}

function applyGlusterConfig {
    if $(isNamedEnvironment ${TARGET}); then
        oc apply -f $(getBuildLocation)/gluster/10-gluster-svc.yml ${SVC_ACCOUNT_CLAUSE}
        oc apply -f $(getBuildLocation)/gluster/11-gluster-endpoints.yml ${SVC_ACCOUNT_CLAUSE}
        oc apply -f $(getBuildLocation)/gluster/named-envs/12-${TARGET}-file-upload-claim.yml ${SVC_ACCOUNT_CLAUSE}
    else
        oc apply -f $(getBuildLocation)/gluster/ ${SVC_ACCOUNT_CLAUSE}
    fi
}

# Entry point
applyConfigMaps
applyRoutes
applyGlusterConfig

