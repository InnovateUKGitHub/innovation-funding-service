#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

if [[ (${TARGET} == "remote") ||  (${TARGET} == "production") ]]
then
    HOST=prod.ifs-test-clusters.com
else
    HOST=ifs-local
fi

SVC_ACCOUNT_TOKEN=${bamboo_openshift_svc_account_token}
SVC_ACCOUNT_CLAUSE="--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://console.prod.ifs-test-clusters.com:443 --insecure-skip-tls-verify=true"

ROUTE_DOMAIN=apps.$HOST
REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com
#REGISTRY=docker-registry-default.apps.dev.ifs-test-clusters.com
INTERNAL_REGISTRY=172.30.80.28:5000

REGISTRY_TOKEN=${SVC_ACCOUNT_TOKEN}


echo "Deploying the $PROJECT Openshift project"

function tailorAppInstance() {
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/prod/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/prod/*.yml

    sed -i.bak "s/<<IMAP-ADDRESS>>/imap-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/imap/*.yml
    sed -i.bak "s/<<ADMIN-ADDRESS>>/admin-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/spring-admin/*.yml

    if [[ ${TARGET} == "production" ]]
    then
        sed -i.bak "s/claimName: file-upload-claim/claimName: production-file-upload-claim/g" os-files-tmp/*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/3*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/4*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/shib/*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/shib/prod/*.yml
    fi
}

function useContainerRegistry() {
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" os-files-tmp/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/innovateuk/#g" os-files-tmp/shib/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/innovateuk/#g" os-files-tmp/shib/prod/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" os-files-tmp/robot-tests/*.yml

    docker tag innovateuk/data-service:1.0-SNAPSHOT \
        ${REGISTRY}/${PROJECT}/data-service:1.0-SNAPSHOT
    docker tag innovateuk/project-setup-service:1.0-SNAPSHOT \
        ${REGISTRY}/${PROJECT}/project-setup-service:1.0-SNAPSHOT
    docker tag innovateuk/project-setup-management-service:1.0-SNAPSHOT \
        ${REGISTRY}/${PROJECT}/project-setup-management-service:1.0-SNAPSHOT
    docker tag innovateuk/competition-management-service:1.0-SNAPSHOT \
        ${REGISTRY}/${PROJECT}/competition-management-service:1.0-SNAPSHOT
    docker tag innovateuk/assessment-service:1.0-SNAPSHOT \
        ${REGISTRY}/${PROJECT}/assessment-service:1.0-SNAPSHOT
    docker tag innovateuk/application-service:1.0-SNAPSHOT \
        ${REGISTRY}/${PROJECT}/application-service:1.0-SNAPSHOT

    docker login -p ${REGISTRY_TOKEN} -e unused -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/data-service:1.0-SNAPSHOT
    docker push ${REGISTRY}/${PROJECT}/project-setup-service:1.0-SNAPSHOT
    docker push ${REGISTRY}/${PROJECT}/project-setup-management-service:1.0-SNAPSHOT
    docker push ${REGISTRY}/${PROJECT}/competition-management-service:1.0-SNAPSHOT
    docker push ${REGISTRY}/${PROJECT}/assessment-service:1.0-SNAPSHOT
    docker push ${REGISTRY}/${PROJECT}/application-service:1.0-SNAPSHOT
}

function deploy() {
    if [[ ${TARGET} == "local" ]]
    then
        oc adm policy add-scc-to-user anyuid -n $PROJECT -z default
    fi

    if [[ ${TARGET} == "production" ]]
    then
        oc create -f os-files-tmp/gluster/10-gluster-svc.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/11-gluster-endpoints.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/production/12-production-file-upload-claim.yml ${SVC_ACCOUNT_CLAUSE}

        oc create -f os-files-tmp/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/prod/56-idp.yml ${SVC_ACCOUNT_CLAUSE}
    else
        oc create -f os-files-tmp/imap/ ${SVC_ACCOUNT_CLAUSE}
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

function cleanUp() {
    rm -rf os-files-tmp
    rm -rf shibboleth
}

function cloneConfig() {
    cp -r os-files os-files-tmp
}

function createProject() {
    until oc new-project $PROJECT ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete project $PROJECT ${SVC_ACCOUNT_CLAUSE} || true
      sleep 10
    done
}

# Entry point
cleanUp
cloneConfig
tailorAppInstance
if [[ ${TARGET} != "production" ]]
then
    createProject
fi

if [[ (${TARGET} == "remote") ||  (${TARGET} == "production") ]]
then
    useContainerRegistry
fi

deploy
blockUntilServiceIsUp

if [[ ${TARGET} != "production" ]]
then
    shibInit
fi
cleanUp
