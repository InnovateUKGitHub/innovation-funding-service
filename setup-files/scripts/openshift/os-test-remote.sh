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
    # [ ! -z "${ROBOT_COMMAND}" ] && ROBOT_COMMAND=", "${ROBOT_COMMAND}
    # sed -i.bak "s#\"./os_run_tests.sh\",\ \"-q\"#\"./os_run_tests.sh\",\ \"-q\"${ROBOT_COMMAND}#g" robot-tests-tmp/Dockerfile

    # [ ! -z "${ROBOT_COMMAND}" ] && ROBOT_COMMAND=" "${ROBOT_COMMAND}

    if [[  ! -z "${ROBOT_COMMAND}" ]]; then
      ROBOT_COMMAND=" "$ROBOT_COMMAND;
      sed -i.bak "s#./os_run_tests.sh\ -q#./os_run_tests.sh\ -q$ROBOT_COMMAND#g" robot-tests-tmp/Dockerfile
    fi

    # sed -i.bak "s#\[\"./os_run_tests.sh\", \"-q\"\]#[\"./os_run_tests.sh\", \"-q\", $ROBOT_COMMAND]#g" robot-tests-tmp/Dockerfile

}

function cleanUp() {
    rm -rf robot-tests-tmp/
    rm -rf $(getBuildLocation)
}

function buildAndPushTestImages() {
    # docker build --build-arg SVC_ACCOUNT_CLAUSE_ARG=${SVC_ACCOUNT_CLAUSE} -t ${REGISTRY}/${PROJECT}/robot-framework:1.0-SNAPSHOT robot-tests-tmp/
    # docker build -t ${REGISTRY}/${PROJECT}/robot-framework:1.0-SNAPSHOT robot-tests-tmp/
    docker build --build-arg GID=${System_env_bamboo_gluster_GID} --build-arg UID=${System_env_bamboo_gluster_UID} --build-arg PW=${System_env_bamboo_gluster_user_password} -t ${REGISTRY}/${PROJECT}/robot-framework:1.0-SNAPSHOT robot-tests-tmp/
    docker login -p ${SVC_ACCOUNT_TOKEN} -u unused ${REGISTRY}
    docker push ${REGISTRY}/${PROJECT}/robot-framework:1.0-SNAPSHOT
}

function deployTests() {
    oc create -f $(getBuildLocation)/robot-tests/7-chrome.yml ${SVC_ACCOUNT_CLAUSE}
    until oc get pods ${SVC_ACCOUNT_CLAUSE} | grep ^chrome | grep -v deploy | grep "Running"; do
        echo "Chrome pod is not running yet.."
        sleep 5
    done
    oc create -f $(getBuildLocation)/robot-tests/8-robot.yml ${SVC_ACCOUNT_CLAUSE}
    until oc get pods ${SVC_ACCOUNT_CLAUSE} | grep ^robot-framework | grep -v deploy | grep "Running"; do
        echo "Robot framework is not running yet.."
        sleep 5
    done
}

function copyNecessaryFiles() {
    cp -r ifs-data-layer/ifs-data-service/docker-build.gradle robot-tests-tmp/docker-build.gradle
    cp -r setup-files/scripts/docker/set-umask0002.sh robot-tests-tmp/set-umask0002.sh
}

function navigateToRoot(){
    scriptDir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

    cd ${scriptDir}
    cd ../../..
}

navigateToRoot
cleanUp
rm -rf robot-tests/target && mkdir robot-tests/target
tailorToAppInstance
copyNecessaryFiles
buildAndPushTestImages
deployTests
cleanUp

echo ""
echo "Tests are running now. You can follow the progress with the following command:"
echo "oc logs -f $(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep robot-framework-1- | grep -v deploy | awk '{ print $1 }')"
