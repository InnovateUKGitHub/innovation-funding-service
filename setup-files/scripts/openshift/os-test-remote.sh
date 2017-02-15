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
    docker build -t ${REGISTRY}/innovateuk/robot-framework:1.0-$PROJECT robot-tests-tmp/
    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAyEwggMdBgkqhkiG9w0BBwagggMOMIIDCgIBADCCAwMGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMxqr9ROZIcTy6ljejAgEQgIIC1Doe/oKmmgD9onhtesuUO5kUrbpAcW8rR8FzvUeCuQxH5CRF5vGnVOQdv2KuP8+cIXEJ7mO/6QlBGWdfnCKdRCdk2zzUlQD9foNk9OKhw6hx/yxjxAhRu5lA9s9f6H/O2Z3tOFMSq1eSaLJXGJU9oTtZqxtC67C/7Z2mQV4IT0rs3/jS7XvN4bmYZT5IU+fD5aQmLhgiu2Z+uPqz3WrjPCk1akbGYeGP2YzYQ585UviWkbnLDv4D8dMCIjOgsWzRTrCE0POv9Cbbn2SS/Ok0NyZzTN5GH6gf67AZL3aNcz1+ktObaGGJwESgUkbjhvCqi6thHlVB8WqQJzFxXC1Q98q3GNtsHyZMPpKpviO8O7CDb7VUk24gha0FnDCxWt8UWGTrmzK+bhkhqPYWlK1pzsEQkv3IIVf66KRwBCUoa55Op03incHaI5LJVS8I15f17gfcFwAMOli6O1v3lLTeyWl1rpyJYHC/Y4lqVP2d5YZ6vmzF3zy6CPGCf1MzjTIc9JHfPGSWrG3W4PlE6CFed5doyF3gQBN14zA0jajiBW/sCQiYRVj8/bABVD2GHzwPW4lq9T3VJ9yTUHVM9mJdNDYeI7Lv9+gicIoJcM4WoIDNjSH7cEzyDmpqw6ZCA/gx+zX+scBWfVk17uqV0Th0dHoDrYnqu0RYudUFcCoVBDS7DWS0DzfNhvjnGE8Eq/SuKPSRPpGJTI8lsFm/0rWPdaGD0HwyzpQxtReW8F9MckwnCO5ogdiOza91w6oMb4vVT+BISEGi63wRSa9Eu3AA4P0rwzc2yrCCP2gzFBWLOsPxAEYi9jXGJgbQRYShmBWj2TALwv67g5qoYmt70JLYlbPP0c3fzCRSNmDl1X6iqEbrjzHlMTIGDfyYlH96ka4hWJRkLEuKaUUJEeB2K14F9FHrjtCLIpl6HX5hqWByLz6QDBmlM6sFa7F/icKctRluARNK3Mg= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com
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
rm -rf robot-tests/target && mkdir robot-tests/target
fileFixtures
tailorToAppInstance
buildAndPushTestImages
deployTests
cleanUp

sleep 5

echo ""
echo "Tests are running now. You can follow the progress with the following command:"
echo "oc logs -f $(oc get pods | grep robot-framework-1- | grep -v deploy | awk '{ print $1 }')"
