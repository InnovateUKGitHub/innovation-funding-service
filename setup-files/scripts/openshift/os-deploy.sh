#!/bin/bash
set -ex

PROJECT=$1
TARGET=$2

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

    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAy0wggMpBgkqhkiG9w0BBwagggMaMIIDFgIBADCCAw8GCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMBiJktO4IxAC0MtlYAgEQgIIC4D66iycDLqf1z0f5fpsSCDONvnxLWV9UWCmLxnyi0ewlS9+GNjYMJdU1/pzsjsG/m3/v+65+TvyB56/n2cYIVF1LnsuEc67Zyqt7YtBdSw89+GRXLsks0bssw17/I6aGfG/6ace2yaCzVvPGs2O38NcZhOT77wgLg4ErdFIGmEMgOUWndBSdXYXZYtnngw6HZ7tYlOGgrpuMcL461W6r5wb6VH7UbFp0W4XlIWQeHQMKfExKCF9iuv61gpDQSsU4EMMvraNEkeRRuXbT8IKm5oVsheBQeksetfhNSgtn/y7OswHzObDRPcG7XLKLKujDK+NyHbX+J9+DN6/lxHNh3hLJ/ZzAc6MEfEUR7l8v8KUgL9j2Smji2q7lw+M33C4emcNUUEaCc0Tqz0/a6v0iVaV7m1lDUSDcz9vy52GztP/24AVPJiM7GdpY9846ZkhHkm+cbZFI6zf99hsKDiHkIJ7Zl2GNClxQgjiU1mx0qmB1F90Xe+6F8xMzNsVgCzfzlXR4/BNFjbarLYwhmyp6M1/1saCAjwZbffGUHhEvtbHu5OIrqVcfRKkD6vWvfWKGlYTpLprcuA2EosNDbBH2fTii2EqYDeZ3SOTVxBfIamU2GPLl2mQxdNE9Y/qTLwagjbLP47JhiuNfBDrLqRQ1WcZtHfIN92wzB7LSaqkTzl8YaQ3aQLqFVCuLZ6t5K0m6rr3a/GmsM+HOT0Hdw7En/l4roYLWRgXJNBUBjEfNXaT7jAFYWEXwH3PaDVF4AuoP36+MnRLL2xFsMSyYId3eXt0hIEcCufR07ueK0XnAjq8LvwQ4JWGLlJ5ZQscIpMMUc4vAXrCK5uJEy+ucMwBNajmwZi4yUbfHP0EVR1/SH9pes5DtChHKK3VcHu91M+fQ08B/WDga46KJ08CNAgbGT714+15XFjkOdyvSYI3nP/6RcRHkBXRAbQeGBp8SVPpd6QM2NEP3vLrv9ohaFEm2Kyk= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com

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
        oc adm policy add-scc-to-user anyuid -n $PROJECT -z default
    else
        chmod 600 setup-files/scripts/openshift/ifs
        ssh-add setup-files/scripts/openshift/ifs
        ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no ec2-user@52.56.119.142 "oc adm policy add-scc-to-user anyuid -n $PROJECT -z default"
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
