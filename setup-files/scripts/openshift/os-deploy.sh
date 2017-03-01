#!/bin/bash
set -ex

PROJECT=$1
TARGET=$2

if [[ ${TARGET} == "production" ]]
then
    PROJECT="production"
fi

if [[ (${TARGET} == "remote") ||  (${TARGET} == "production") ]]
then
    HOST=prod.ifs-test-clusters.com
else
    HOST=ifs-local
fi

ROUTE_DOMAIN=apps.$HOST
REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com
#REGISTRY=docker-registry-default.apps.dev.ifs-test-clusters.com
INTERNAL_REGISTRY=172.30.80.28:5000


echo "Deploying the $PROJECT Openshift project"

function tailorAppInstance() {
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/prod/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/prod/*.yml

    sed -i.bak "s/<<IMAP-ADDRESS>>/imap-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/imap/*.yml
    sed -i.bak "s/<<ADMIN-ADDRESS>>/admin-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml

    sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/3*.yml
    sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/4*.yml
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

    docker login -p $(oc whoami -t) -e unused -u unused ${REGISTRY}

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
        oc create -f os-files-tmp/
        oc create -f os-files-tmp/shib/5-shib.yml
        oc create -f os-files-tmp/shib/prod/56-idp.yml
    else
        oc create -f os-files-tmp/imap/
        oc create -f os-files-tmp/mysql/
        oc create -f os-files-tmp/shib/
        oc create -f os-files-tmp/
    fi
}

function blockUntilServiceIsUp() {
    UNREADY_PODS=1
    while [ ${UNREADY_PODS} -ne "0" ]
    do
        UNREADY_PODS=$(oc get pods -o custom-columns='NAME:{.metadata.name},READY:{.status.conditions[?(@.type=="Ready")].status}' | grep -v True | sed 1d | wc -l)
        oc get pods
        echo "$UNREADY_PODS pods still not ready"
        sleep 5s
    done
    oc get routes
}

function shibInit() {
     oc rsh $(oc get pods | awk '/ldap/ { print $1 }') /usr/local/bin/ldap-sync-from-ifs-db.sh ifs-database
}

function cleanUp() {
    rm -rf os-files-tmp
    rm -rf shibboleth
}

function cloneConfig() {
    cp -r os-files os-files-tmp
}

function createProject() {
    until oc new-project $PROJECT
    do
      oc delete project $PROJECT || true
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
else
    oc project $PROJECT
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
