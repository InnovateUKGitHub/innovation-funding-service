#!/bin/bash
set -e

ENV=$1
HOST=dev.ifs-test-clusters.com

echo "Deploying tests to the $ENV Openshift environment"


function tailorToAppInstance() {
    rm -rf os-files-tmp
    cp -r os-files os-files-tmp
    sed -i.bak "s#worth/#721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/#g" os-files-tmp/robot-tests/*.yml
    sed -i.bak "s/<<SHIB-ADDRESS>>/$ENV.$HOST/g" os-files-tmp/robot-tests/*.yml
    sed -i.bak "s/1.0-SNAPSHOT/1.0-$ENV/g" os-files-tmp/robot-tests/*.yml

    cp -r robot-tests robot-tests-tmp
    sed -i.bak "s/<<SHIB-ADDRESS>>/$ENV.$HOST/g" robot-tests-tmp/micro_run_tests.sh
}

function cleanUp() {
    rm -rf robot-tests-tmp/
    rm -rf os-files-tmp
}

function buildAndPushTestImages() {
    rm -rf robot-tests/target && mkdir robot-tests/target
    docker build -t 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/robot-framework:1.0-$ENV robot-tests-tmp/
    docker push 721685138178.dkr.ecr.eu-west-2.amazonaws.com/worth/robot-framework:1.0-$ENV
}

function deployTests() {
    oc create -f os-files-tmp/robot-tests/7-selenium-grid.yml
    sleep 5
    oc create -f os-files-tmp/robot-tests/8-robot.yml
    sleep 2
}

cleanUp
tailorToAppInstance
buildAndPushTestImages
deployTests
cleanUp

echo ""
echo "Tests are running now. You can follow the progress with the wollowing command:"
echo "oc logs -f $(oc get pods | grep robot-framework | awk '{ print $1 }')"
