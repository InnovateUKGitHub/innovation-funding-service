#!/bin/bash
set -e

PROJECT=$(oc project -q)
shift 1
ROBOT_COMMAND=$@
HOST=prod.ifs-test-clusters.com
ROUTE_DOMAIN=apps.${HOST}
REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com
INTERNAL_REGISTRY=172.30.80.28:5000

echo "Deploying tests to the current oc project ($PROJECT)"

function tailorToAppInstance() {
    rm -rf os-files-tmp
    cp -r os-files os-files-tmp
    sed -i.bak "s#innovateuk/#${INTERNAL_REGISTRY}/${PROJECT}/#g" os-files-tmp/robot-tests/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" os-files-tmp/robot-tests/*.yml

    cp -r robot-tests robot-tests-tmp
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" robot-tests-tmp/openshift/*.sh
    sed -i.bak "s/<<SHIB-ADDRESS>>/$PROJECT.$ROUTE_DOMAIN/g" robot-tests-tmp/os_run_tests.sh
    sed -i.bak "s#\[\"./os_run_tests.sh\", \"-q\"\]#[\"./os_run_tests.sh\", \"-q\", \"$ROBOT_COMMAND\"]#g" robot-tests-tmp/Dockerfile

}

function cleanUp() {
    rm -rf robot-tests-tmp/
    rm -rf os-files-tmp
}

function buildAndPushTestImages() {
    docker build -t ${REGISTRY}/${PROJECT}/robot-framework:1.0-SNAPSHOT robot-tests-tmp/
    docker login -p $(oc whoami -t) -e unused -u unused ${REGISTRY}
    docker push ${REGISTRY}/${PROJECT}/robot-framework:1.0-SNAPSHOT
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

function copyNecessaryFiles() {
    cp -r ifs-data-service/docker-build.gradle robot-tests-tmp/docker-build.gradle
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
echo "oc logs -f $(oc get pods | grep robot-framework-1- | grep -v deploy | awk '{ print $1 }')"
