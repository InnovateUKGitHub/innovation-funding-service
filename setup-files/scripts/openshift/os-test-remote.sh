#!/bin/bash
set -e

PROJECT=$1
HOST=prod.ifs-test-clusters.com
ROUTE_DOMAIN=apps.$HOST
REGISTRY=721685138178.dkr.ecr.eu-west-2.amazonaws.com

echo "Deploying tests to the $PROJECT Openshift environment"

function tailorToAppInstance() {
    rm -rf os-files-tmp
    cp -r os-files os-files-tmp
    sed -i.bak "s#innovateuk/#${REGISTRY}/innovateuk/#g" os-files-tmp/robot-tests/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/robot-tests/*.yml
    sed -i.bak "s/1.0-SNAPSHOT/1.0-$PROJECT/g" os-files-tmp/robot-tests/*.yml

    cp -r robot-tests robot-tests-tmp
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" robot-tests-tmp/openshift/*.sh
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" robot-tests-tmp/os_run_tests.sh
}

function cleanUp() {
    rm -rf robot-tests-tmp/
    rm -rf os-files-tmp
}

function buildAndPushTestImages() {
    rm -rf robot-tests/target && mkdir robot-tests/target
    sleep 5
    docker build -t ${REGISTRY}/innovateuk/robot-framework:1.0-$PROJECT robot-tests-tmp/
    docker push ${REGISTRY}/innovateuk/robot-framework:1.0-$PROJECT
}

function deployTests() {
    oc create -f os-files-tmp/robot-tests/7-selenium-grid.yml
    sleep 5
    oc create -f os-files-tmp/robot-tests/8-robot.yml
    sleep 2
}

function fileFixtures() {
    chmod +x robot-tests/openshift/addtestFiles.sh
    ./robot-tests/openshift/addtestFiles.sh
}

cleanUp
fileFixtures
tailorToAppInstance
buildAndPushTestImages
deployTests
cleanUp

echo ""
echo "Tests are running now. You can follow the progress with the wollowing command:"
echo "oc logs -f $(oc get pods | grep robot-framework-1 | awk '{ print $1 }')"
