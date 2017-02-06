#!/bin/bash
set -ex

PROJECT=$1
TARGET=$2
OSX=$3

if [[ ${TARGET} == "remote" ]]
then
    HOST=prod.ifs-test-clusters.com
else
    HOST=ifs-local
fi

ROUTE_DOMAIN=apps.$HOST
REGISTRY=721685138178.dkr.ecr.eu-west-2.amazonaws.com

echo "Deploying the $PROJECT Openshift PROJECTironment"

function tailorAppInstance() {
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<IMAP-ADDRESS>>/imap-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<ADMIN-ADDRESS>>/admin-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" shib-init/*.sh
}

function useContainerRegistry() {
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/init/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s/1.0-SNAPSHOT/1.0-$PROJECT/g" os-files-tmp/*.yml
    sed -i.bak "s/1.0-SNAPSHOT/1.0-$PROJECT/g" os-files-tmp/init/*.yml
    sed -i.bak "s/1.0-SNAPSHOT/1.0-$PROJECT/g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s# innovateuk/# ${REGISTRY}/innovateuk/#g" os-files-tmp/*.yml
    sed -i.bak "s# innovateuk/# ${REGISTRY}/innovateuk/#g" os-files-tmp/init/*.yml
    sed -i.bak "s# innovateuk/# ${REGISTRY}/innovateuk/#g" os-files-tmp/robot-tests/*.yml

    docker tag innovateuk/data-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/data-service:1.0-$PROJECT
    docker tag innovateuk/project-setup-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/project-setup-service:1.0-$PROJECT
    docker tag innovateuk/project-setup-management-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/project-setup-management-service:1.0-$PROJECT
    docker tag innovateuk/competition-management-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/competition-management-service:1.0-$PROJECT
    docker tag innovateuk/assessment-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/assessment-service:1.0-$PROJECT
    docker tag innovateuk/application-service:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/application-service:1.0-$PROJECT
    docker tag innovateuk/shib-init:1.0-SNAPSHOT \
        ${REGISTRY}/innovateuk/shib-init:1.0-$PROJECT

    docker push ${REGISTRY}/innovateuk/data-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/project-setup-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/project-setup-management-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/competition-management-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/assessment-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/application-service:1.0-$PROJECT
    docker push ${REGISTRY}/innovateuk/shib-init:1.0-$PROJECT
}

function buildShibInit() {
    docker build -t innovateuk/shib-init:1.0-SNAPSHOT shib-init
}

function deploy() {
    oc new-project $PROJECT

    oc create -f os-files-tmp/1-aws-registry-secret.yml
    oc secrets add serviceaccount/default secrets/aws-secret-2 --for=pull
    rm -rf os-files-tmp/1-aws-registry-secret.yml

    if [[ ${TARGET} == "local" ]]
    then
        if [ "$OSX" -eq true ]
        then
            oc adm policy add-scc-to-user anyuid -n $PROJECT -z default
        else
            oc adm policy add-scc-to-user anyuid -n $PROJECT -z default --config=/var/lib/origin/openshift.local.config/master/admin.kubeconfig
        fi
    else
        chmod 600 setup-files/scripts/openshift/ifs
        ssh-add setup-files/scripts/openshift/ifs
        ssh ec2-user@52.56.119.142 "oc adm policy add-scc-to-user anyuid -n $PROJECT -z default"
    fi


    oc create -f os-files-tmp/
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
    oc rsh $(oc get pods | grep ldap | awk '{ print $1 }') /usr/local/bin/ldap-delete-all-users.sh
    oc create -f os-files-tmp/init/6-shib-init.yml
}

function cleanUp() {
    rm -rf os-files-tmp
    rm -rf shibboleth
    rm -rf shib-init
}

function cloneConfig() {
    cp -r os-files os-files-tmp
    cp -r setup-files/scripts/openshift/shib-init shib-init
}

cleanUp
cloneConfig
tailorAppInstance
buildShibInit
if [[ ${TARGET} == "remote" ]]
then
    useContainerRegistry
fi
deploy
blockUntilServiceIsUp
shibInit
cleanUp
