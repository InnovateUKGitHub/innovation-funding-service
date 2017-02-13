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

    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAy0wggMpBgkqhkiG9w0BBwagggMaMIIDFgIBADCCAw8GCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQM3ZeSRp5ovr7fIuAaAgEQgIIC4DKOFXyew9JSqSK+0S7HTN5VcXXZj0x8lViDcuNdmRJwl4CadTIQxNIobic1syGNIF/+TxTJNj4E3LlfYkpxxwNT3yzug1uP3nnc65xn5bZ+VrVHtSnljYEBIT7vu7MuS8ibseCAFVuQrtiPYZebc5sx6ua+EGBb8auXHbA6pGYvsbu3P4dCqKsTCvOa+Ow1GkHLjaSJGFRyYzJVXL79dHap1h5+afBGgDE8ggu0j3BqVlznq28pF7uwkQY7ULsuVmouVM5weAvgY7uXnYWraV/7I/yIZJdi2Na9hOHB2mIADGzrTozlurIW1bJouZdExol5RFuhtDZMf+dUCWTG2EfP7wehB3hvl2Uvz7ay8Z2CTAJf5d5svmffeJa+Y5thzgDkVeP2UjxKQqBG7YRN1VxBo8EUv+VvXoKyia2c3tIFD2eX7reQ1B9gpLg/Fg8HlE90bVN/pemHVkcZLfBYYc2scx1YH9a8bz/ZRZ6KsXaZ0bRWQ0gbPmPBDZbsUYpJ2RgSF7ahhB8Wc9JswL96ssKqvnh2SoRNjVdG+j7SxmFyaY1kJkY49sfyjouj88PYwf54LDltsDeOF1ui8OBPIbUEfprLW0Ks2NmmWQ+SermWwqGVsJBQY87tG+cqNRbQFXwF3LLc2+imth4U9mceiYBzJeoH8iRq+QGNZW9p1bsYZjIDAWsA4LoIrnAGgXtpNDUxmNk+0s8eOzm6sb5h3ojxq5S7DM/suIpmwQdWcnZ2JdD6zhjYPTaaLbF/nhPWBIOvDhrHuYn5Urej7ucKMXc+C+SatI3sDghHSUTyzvfSrrn6QXUH3YmXEOC0EpYfA3ysG9+UvUVyhuvrIbZCzBtMeE2E6ZLaLb2HgO3GX4TP/wyU4GUD9DDOoSWzOrvaGeDuA2Wbqch4p/t3Igzwo9c6PC7hZxvzjl4ASC7wWvNjBcvhw99Iky5jaNCauCcoN+PTF//DND2eKycS8hL+bDk= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com

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
