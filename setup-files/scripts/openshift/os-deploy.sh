#!/bin/bash

set -e
set -x

PROJECT=$1
TARGET=$2
VERSION=$3

if [[ (${TARGET} == "local") ]]; then
  HOST=ifs-local
  ROUTE_DOMAIN=apps.$HOST
elif [[ ${TARGET} == "production" ]]; then
  HOST=apply-for-innovation-funding.service.gov.uk
  ROUTE_DOMAIN=$HOST
  PROJECT="production"
else
  HOST=prod.ifs-test-clusters.com
  ROUTE_DOMAIN=apps.$HOST
fi

if [[ ${TARGET} == "demo" ]]; then PROJECT="demo"; fi
if [[ ${TARGET} == "uat" ]]; then PROJECT="uat"; fi
if [[ ${TARGET} == "sysint" ]]; then PROJECT="sysint"; fi
if [[ ${TARGET} == "perf" ]]; then PROJECT="perf"; fi

if [ -z "$bamboo_openshift_svc_account_token" ]; then  SVC_ACCOUNT_TOKEN=$(oc whoami -t); else SVC_ACCOUNT_TOKEN=${bamboo_openshift_svc_account_token}; fi


if [[ (${TARGET} == "local") ]]; then
    SVC_ACCOUNT_CLAUSE_SERVER_PART='localhost:8443'
else
    SVC_ACCOUNT_CLAUSE_SERVER_PART='console.prod.ifs-test-clusters.com:443'
fi

SVC_ACCOUNT_CLAUSE="--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://${SVC_ACCOUNT_CLAUSE_SERVER_PART} --insecure-skip-tls-verify=true"
REGISTRY_TOKEN=${SVC_ACCOUNT_TOKEN};

if [[ (${TARGET} == "local") ]]; then
    REGISTRY=$($(dirname $0)/os-get-registry-url.sh)
else
    REGISTRY='docker-registry-default.apps.prod.ifs-test-clusters.com'
fi

if [[ (${TARGET} == "local") ]]; then
    INTERNAL_REGISTRY=$REGISTRY
else
    INTERNAL_REGISTRY=172.30.80.28:5000
fi

echo "Deploying the $PROJECT Openshift project"


function deploy() {

    if [[ ${TARGET} == "local" ]]
    then
        $(dirname $0)/os-allow-shibboleth-to-be-run-as-root.sh
    fi

    if [[ ${TARGET} == "production" || ${TARGET} == "demo" || ${TARGET} == "uat" || ${TARGET} == "sysint" || ${TARGET} == "perf" ]]
    then
        oc create -f os-files-tmp/gluster/10-gluster-svc.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/11-gluster-endpoints.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/named-envs/12-${TARGET}-file-upload-claim.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/named-envs/56-${TARGET}-idp.yml ${SVC_ACCOUNT_CLAUSE}
    else
        oc create -f os-files-tmp/mail/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/mysql/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/spring-admin/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/ ${SVC_ACCOUNT_CLAUSE}
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

function createProject() {
    until oc new-project $PROJECT ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete project $PROJECT ${SVC_ACCOUNT_CLAUSE} || true
      sleep 10
    done
}

 . $(dirname $0)/deploy-functions.sh

# Entry point
cleanUp
cloneConfig
tailorAppInstance
if [[ ${TARGET} != "production" && ${TARGET} != "demo" && ${TARGET} != "uat" && ${TARGET} != "sysint" && ${TARGET} != "perf" ]]
then
    createProject
fi

useContainerRegistry
pushApplicationImages
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
