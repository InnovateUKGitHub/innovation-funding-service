#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

if [[ ${TARGET} == "production" ]]
then
    PROJECT="production"
fi

if [[ ${TARGET} == "demo" ]]
then
    PROJECT="demo"
fi

if [[ ${TARGET} == "uat" ]]
then
    PROJECT="uat"
fi


if [[ (${TARGET} == "local") ]]
then
    HOST=ifs-local
else
    HOST=prod.ifs-test-clusters.com
fi

SVC_ACCOUNT_TOKEN=${bamboo_openshift_svc_account_token}
LDAP_PASSWORD=${bamboo_ldap_password}

SVC_ACCOUNT_CLAUSE="--namespace=${PROJECT} --token=${SVC_ACCOUNT_TOKEN} --server=https://console.prod.ifs-test-clusters.com:443 --insecure-skip-tls-verify=true"

ROUTE_DOMAIN=apps.$HOST
REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com
INTERNAL_REGISTRY=172.30.80.28:5000

if [ -z "$SVC_ACCOUNT_TOKEN" ]
then
    REGISTRY_TOKEN=$(oc whoami -t)
else
    REGISTRY_TOKEN=${SVC_ACCOUNT_TOKEN}
fi

echo "Deploying the $PROJECT Openshift project"

function convertFileToBlock() {
   cat $1 | tail -n +2 | sed '$ d' | tr -d '\r' | tr '\n' ' ' |  rev | cut -c 2- | rev
}

function tailorAppInstance() {
    if [ -z "$SSLCERTFILE" ]; then echo "Set SSLCERTFILE, SSLCACERTFILE, and SSLKEYFILE environment variables"; exit -1; fi
    sed -i.bak $"s#<<SSLCERT>>#$(convertFileToBlock $SSLCERTFILE)#g" os-files-tmp/shib/*.yml
    sed -i.bak $"s#<<SSLCERT>>#$(convertFileToBlock $SSLCERTFILE)#g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak $"s#<<SSLCACERT>>#$(convertFileToBlock $SSLCACERTFILE)#g" os-files-tmp/shib/*.yml
    sed -i.bak $"s#<<SSLCACERT>>#$(convertFileToBlock $SSLCACERTFILE)#g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak $"s#<<SSLKEY>>#$(convertFileToBlock $SSLKEYFILE)#g" os-files-tmp/shib/*.yml
    sed -i.bak $"s#<<SSLKEY>>#$(convertFileToBlock $SSLKEYFILE)#g" os-files-tmp/shib/named-envs/*.yml

    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/*.yml
    sed -i.bak "s/<<SHIB-IDP-ADDRESS>>/auth-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/shib/named-envs/*.yml

    sed -i.bak "s/<<IMAP-ADDRESS>>/imap-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/imap/*.yml
    sed -i.bak "s/<<ADMIN-ADDRESS>>/admin-$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/spring-admin/*.yml

    if [[ ${TARGET} == "production" || ${TARGET} == "demo" || ${TARGET} == "uat" ]]
    then
        sed -i.bak "s/claimName: file-upload-claim/claimName: ${TARGET}-file-upload-claim/g" os-files-tmp/*.yml
        if [ -z "$LDAP_PASSWORD" ]; then echo "Set LDAP_PASSWORD environment variable"; exit -1; fi
        sed -i.bak "s/<<LDAP-PASSWORD>>/${LDAP_PASSWORD}/g" os-files-tmp/shib/named-envs/*.yml

    fi

    if [[ ${TARGET} == "production" || ${TARGET} == "uat" ]]
    then
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/3*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/4*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/shib/*.yml
        sed -i.bak "s/replicas: 1/replicas: 2/g" os-files-tmp/shib/named-envs/*.yml
    fi
}

function useContainerRegistry() {
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/*.yml
    sed -i.bak "s/imagePullPolicy: IfNotPresent/imagePullPolicy: Always/g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" os-files-tmp/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/innovateuk/#g" os-files-tmp/shib/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/innovateuk/#g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak "s# innovateuk/# ${INTERNAL_REGISTRY}/${PROJECT}/#g" os-files-tmp/robot-tests/*.yml

    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" os-files-tmp/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" os-files-tmp/shib/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" os-files-tmp/shib/named-envs/*.yml
    sed -i.bak "s#1.0-SNAPSHOT#${VERSION}#g" os-files-tmp/robot-tests/*.yml

    docker tag innovateuk/data-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/data-service:${VERSION}
    docker tag innovateuk/project-setup-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/project-setup-service:${VERSION}
    docker tag innovateuk/project-setup-management-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}
    docker tag innovateuk/competition-management-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/competition-management-service:${VERSION}
    docker tag innovateuk/assessment-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/assessment-service:${VERSION}
    docker tag innovateuk/application-service:${VERSION} \
        ${REGISTRY}/${PROJECT}/application-service:${VERSION}

    docker login -p ${REGISTRY_TOKEN} -e unused -u unused ${REGISTRY}

    docker push ${REGISTRY}/${PROJECT}/data-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/project-setup-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/project-setup-management-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/competition-management-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/assessment-service:${VERSION}
    docker push ${REGISTRY}/${PROJECT}/application-service:${VERSION}
}

function deploy() {
    if [[ ${TARGET} == "local" ]]
    then
        oc adm policy add-scc-to-user anyuid -n $PROJECT -z default
    fi

    if [[ ${TARGET} == "production" || ${TARGET} == "demo" || ${TARGET} == "uat" ]]
    then
        oc create -f os-files-tmp/gluster/10-gluster-svc.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/11-gluster-endpoints.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/gluster/named-envs/12-${TARGET}-file-upload-claim.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/ ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
        oc create -f os-files-tmp/shib/named-envs/56-${TARGET}-idp.yml ${SVC_ACCOUNT_CLAUSE}
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

if [[ (${TARGET} != "local") ]]
then
    useContainerRegistry
fi

deploy
blockUntilServiceIsUp

if [[ ${TARGET} == "local" || ${TARGET} == "remote" ]]
then
    shibInit
fi
cleanUp
