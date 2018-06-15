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

echo "Deploying the $PROJECT Openshift project"

function deploy() {

    if $(isNamedEnvironment ${TARGET}); then
        oc create -f $(getBuildLocation)/gluster/10-gluster-svc.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/gluster/11-gluster-endpoints.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/gluster/named-envs/12-${TARGET}-file-upload-claim.yml ${SVC_ACCOUNT_CLAUSE}
    else
        oc create -f $(getBuildLocation)/shib/55-ldap.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mail/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mysql/3-mysql.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/mysql/3-finance-totals-mysql.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/gluster/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f $(getBuildLocation)/spring-admin/ ${SVC_ACCOUNT_CLAUSE}

        # conditionally deploy prototypes service
        if $(isServiceEnabled "ifs.prototypes"); then
            oc create -f $(getBuildLocation)/prototypes/46-prototypes-service.yml ${SVC_ACCOUNT_CLAUSE}
        fi

    fi

    # The SIL stub is required in all environments, in one form or another, except for production
    if ! $(isProductionEnvironment ${TARGET}); then
        oc create -f $(getBuildLocation)/sil-stub/ ${SVC_ACCOUNT_CLAUSE}
    fi

    # Only named environment for Prototypes is SysInt
    if $(isSysIntEnvironment ${TARGET}); then
        oc create -f $(getBuildLocation)/prototypes/46-prototypes-service.yml ${SVC_ACCOUNT_CLAUSE}
    fi

    oc create -f $(getBuildLocation)/ ${SVC_ACCOUNT_CLAUSE}
    oc create -f $(getBuildLocation)/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
    oc create -f $(getBuildLocation)/shib/56-idp.yml ${SVC_ACCOUNT_CLAUSE}
}

function shibInit() {
     oc rsh ${SVC_ACCOUNT_CLAUSE} $(oc get pods  ${SVC_ACCOUNT_CLAUSE} | awk '/ldap/ { print $1 }') /usr/local/bin/ldap-sync-from-ifs-db.sh ifs-database
}

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

if [[ ${TARGET} == "production" || ${TARGET} == "uat" ]]
then
    # We only scale up data-services once started up and performed the Flyway migrations on one thread
    scaleDataService
    scaleFinanceDataService
fi

