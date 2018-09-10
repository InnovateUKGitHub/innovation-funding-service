#!/bin/bash

set -e

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

PROJECT=$1
shift 1
ROBOT_COMMAND=$@
HOST=$(getClusterAddress)
ROUTE_DOMAIN=apps.${HOST}
REGISTRY=$(getRegistry)
INTERNAL_REGISTRY=$(getInternalRegistry)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause "remote" $PROJECT $SVC_ACCOUNT_TOKEN)

echo "Deploying tests to ($PROJECT)"

function tailorToAppInstance() {
    rm -rf $(getBuildLocation)
    echo "Starting to copy os-files to $(getBuildLocation)"
    mkdir -p $(getBuildLocation)
    cp -r os-files/* $(getBuildLocation)
    sed -i.bak "s#innovateuk/#${INTERNAL_REGISTRY}/${PROJECT}/#g" $(getBuildLocation)/robot-tests/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" $(getBuildLocation)/robot-tests/*.yml

    cp -r robot-tests robot-tests-tmp
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" robot-tests-tmp/openshift/*.sh
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" robot-tests-tmp/os_run_tests.sh
    echo "ROBOT COMMAND: $ROBOT_COMMAND"
    sed -i.bak "s#\[\"./os_run_tests.sh\", \"-q\"\]#[\"./os_run_tests.sh\", \"-q\", $ROBOT_COMMAND]#g" robot-tests-tmp/Dockerfile
}

function cleanUp() {
    rm -rf robot-tests-tmp/
    rm -rf $(getBuildLocation)
}

function buildAndPushTestImages() {
    docker build -t ${REGISTRY}/${PROJECT}/robot-framework:1.0-SNAPSHOT robot-tests-tmp/
    docker login -p ${SVC_ACCOUNT_TOKEN} -u unused ${REGISTRY}
    docker push ${REGISTRY}/${PROJECT}/robot-framework:1.0-SNAPSHOT
}

function deployTests() {
    oc create -f $(getBuildLocation)/robot-tests/7-chrome.yml ${SVC_ACCOUNT_CLAUSE}
    sleep 30 # TODO should wait till chrome is running
    oc create -f $(getBuildLocation)/robot-tests/8-robot.yml ${SVC_ACCOUNT_CLAUSE}
    sleep 2
}

function fileFixtures() {
    chmod +x robot-tests/openshift/addtestFiles.sh
    ./robot-tests/openshift/addtestFiles.sh "${SVC_ACCOUNT_CLAUSE}"
}

function copyNecessaryFiles() {
    cp -r ifs-data-layer/ifs-data-service/docker-build.gradle robot-tests-tmp/docker-build.gradle
}

function navigateToRoot(){
    scriptDir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

    cd ${scriptDir}
    cd ../../..
}

navigateToRoot
cleanUp
rm -rf robot-tests/target && mkdir robot-tests/target
fileFixtures
tailorToAppInstance
copyNecessaryFiles
buildAndPushTestImages
deployTests
cleanUp

sleep 5

echo ""
echo "Tests are running now. You can follow the progress with the following command:"
echo "oc logs -f $(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep robot-framework-1- | grep -v deploy | awk '{ print $1 }')"
