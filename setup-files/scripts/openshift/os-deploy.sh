#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3
FRACTAL_ENABLED=$4

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

    if [[ ${TARGET} == "production" || ${TARGET} == "demo" || ${TARGET} == "uat" || ${TARGET} == "sysint" || ${TARGET} == "perf" ]]
    then
        oc create -f os-files-tmp/gluster/10-gluster-svc.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/11-gluster-endpoints.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/named-envs/12-${TARGET}-file-upload-claim.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/named-envs/56-${TARGET}-idp.yml ${SVC_ACCOUNT_CLAUSE}

     if [[${TARGET} == "sysint" ]]
     then
        oc create -f os-files-tmp/fractal/ ${SVC_ACCOUNT_CLAUSE}
      fi

    else
        oc create -f os-files-tmp/mail/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/mysql/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/spring-admin/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/ ${SVC_ACCOUNT_CLAUSE}

    if [[ ${FRACTAL_ENABLED} == "true" ]]
    then
        oc create -f os-files-tmp/fractal/ ${SVC_ACCOUNT_CLAUSE}
    fi

    fi

}

function blockUntilServiceIsUp() {
    UNREADY_PODS=1
    while [ ${UNREADY_PODS} -ne "0" ]
    do
        UNREADY_PODS=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} -o custom-columns='NAME:{.metadata.name},READY:{.status.conditions[?(@.type=="Ready")].status}' | grep -v True | sed 1d | wc -l)
        oc get pods ${SVC_ACCOUNT_CLAUSE}
        echo "$UNREADY_PODS pods still not ready"
        sleep 5s
    done
    oc get routes ${SVC_ACCOUNT_CLAUSE}
}

function shibInit() {
     oc rsh ${SVC_ACCOUNT_CLAUSE} $(oc get pods  ${SVC_ACCOUNT_CLAUSE} | awk '/ldap/ { print $1 }') /usr/local/bin/ldap-sync-from-ifs-db.sh ifs-database
}

# Entry point
cleanUp
cloneConfig
tailorAppInstance
useContainerRegistry
deploy
blockUntilServiceIsUp

if [[ ${TARGET} == "local" || ${TARGET} == "remote" ]]
then
    shibInit
fi

if [[ ${TARGET} == "production" || ${TARGET} == "uat" ]]
then
    # We only scale up data-service once data-service started up and performed the Flyway migrations on one thread
    scaleDataService
fi

cleanUp
