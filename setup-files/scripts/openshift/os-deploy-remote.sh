#!/bin/bash
set -e

ENV=$1
HOST=dev.ifs-test-clusters.com

echo "Deploying the $ENV Openshift environment"

function tailorAppInstance() {
    cp -r os-files os-files-tmp
    sed -i.bak "s#worth/#721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/#g" os-files-tmp/*.yml
    sed -i.bak "s#worth/#721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/#g" os-files-tmp/init/*.yml
    sed -i.bak "s#worth/#721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/#g" os-files-tmp/robot-tests/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$ENV.$HOST/g" os-files-tmp/*.yml
    sed -i.bak "s/<<IMAP-ADDRESS>>/imap-$ENV.$HOST/g" os-files-tmp/*.yml
    sed -i.bak "s/<<ADMIN-ADDRESS>>/admin-$ENV.$HOST/g" os-files-tmp/*.yml

    sed -i.bak "s/1.0-SNAPSHOT/1.0-$ENV/g" os-files-tmp/*.yml
    sed -i.bak "s/1.0-SNAPSHOT/1.0-$ENV/g" os-files-tmp/init/*.yml
    sed -i.bak "s/1.0-SNAPSHOT/1.0-$ENV/g" os-files-tmp/robot-tests/*.yml
}

function buildShib() {
    cp -r setup-files/scripts/docker/shibboleth shibboleth
    sed -i.bak "s/<<HOSTNAME>>/$ENV.$HOST/g" shibboleth/*
    docker build -t 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/shibboleth:1.0-$ENV shibboleth/
}

function buildShibInit() {
    cp -r setup-files/scripts/openshift/shib-init shib-init
    sed -i.bak "s/<<SHIB-ADDRESS>>/$ENV.$HOST/g" shib-init/*.sh
    docker build -t 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/shib-init:1.0-$ENV shib-init
}

function buildRobotTests() {
    docker build -t 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/robot-framework:1.0-$ENV robot-tests/
}

function retagAppImages() {
    docker tag worth/data-service:1.0-SNAPSHOT \
        721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/data-service:1.0-$ENV
    docker tag worth/project-setup-service:1.0-SNAPSHOT \
        721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/project-setup-service:1.0-$ENV
    docker tag worth/project-setup-management-service:1.0-SNAPSHOT \
        721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/project-setup-management-service:1.0-$ENV
    docker tag worth/competition-management-service:1.0-SNAPSHOT \
        721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/competition-management-service:1.0-$ENV
    docker tag worth/assessment-service:1.0-SNAPSHOT \
        721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/assessment-service:1.0-$ENV
    docker tag worth/application-service:1.0-SNAPSHOT \
        721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/application-service:1.0-$ENV
}

function pushImages() {
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/data-service:1.0-$ENV
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/project-setup-service:1.0-$ENV
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/project-setup-management-service:1.0-$ENV
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/competition-management-service:1.0-$ENV
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/assessment-service:1.0-$ENV
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/application-service:1.0-$ENV
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/shibboleth:1.0-$ENV
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/shib-init:1.0-$ENV
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/robot-framework:1.0-$ENV
}

function deploy() {
    oc new-project $ENV
    oc create -f os-files-tmp/1-aws-registry-secret.yml
    oc secrets add serviceaccount/default secrets/aws-secret-2 --for=pull
    rm -rf os-files-tmp/1-aws-registry-secret.yml
    #oc adm policy add-scc-to-user anyuid -n $ENV -z default --config=setup-files/scripts/openshift/admin.kubeconfig
    ssh-add setup-files/scripts/openshift/ifs
    ssh ec2-user@52.56.92.144 "oc adm policy add-scc-to-user anyuid -n $ENV -z default"

    oc create -f os-files-tmp/
}

function blockUntilServiceIsUp() {
    SERVICE_STATUS=404
    while [ ${SERVICE_STATUS} -ne "200" ]
    do
        SERVICE_STATUS=$(curl  --max-time 1 -k -L -s -o /dev/null -w "%{http_code}" https://${ENV}.${HOST}/) || true
        oc get pods
        echo "Service status: HTTP $SERVICE_STATUS"
        sleep 5s
    done
    oc get routes
}

function shibInit() {
    oc create -f os-files-tmp/init/6-shib-init.yml
}

function cleanUp() {
    rm -rf os-files-tmp
    rm -rf shibboleth
    rm -rf shib-init
}

cleanUp
tailorAppInstance
buildShib
buildShibInit
buildRobotTests
retagAppImages
pushImages
deploy
blockUntilServiceIsUp
shibInit
cleanUp
