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
    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAyEwggMdBgkqhkiG9w0BBwagggMOMIIDCgIBADCCAwMGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMVFtyjirJJII8+LhmAgEQgIIC1MWJ8OP82l1WT2lgElhPG+A3fsqtbV+wL4dOkuA8f7ZExPrDeTot+RcMuaJAMPYpud+Bc6U3CCXdBT87hFsf6rJBHwHQQ5H2TV7TNhuNcDHea2FwkQlv8L4CuajohE/BCZsPmtdnCU7yWKmeMLKFIt9ugDeDRDDlPEcGL+yH/PKVbldzY/SAIdSQDAUyrNGiVB/NDjhDKPZNbYNEqlZl0EBqwnkIStFrW4PRsEmOLsYmXgBJqYZB8KXshVBp8kHZzAWnSri9XSCxfKPFAwXnldnVBsDSzzEPInH0TC7YPw+MffIqIGMfgnrQypF0yhij0SQVJZolIRPIOkjlFa5OO/+JqmPdEaHq3GyVtFJaKUPg4gQm1zd1rF6I6SON4/wFjRUYlawPH8Q9fZeASzyetoamUcQT1HjjY/SEYgnewr6RBN2oRfzOZG02wWou9PS36Hjej1LgmWBdZ7tKvfGXgm5x77M1CcchIvuf5unTIjWpmnzu9LlRFJVIWTmRh0E0KC6sr+9pjHkGbgV4YwjOhEXrp2QAbNQEwCRru8tedYOFrhz7IsZbq67IhrbWb+PcV27wPgdGGm+6obBNOeINBcNBfv9qlGkp2iWO2dktgoRPg0WGkTrjoOSdK6r7ZsVTflDC68kjGIIS0B9SLkAp/INM5ywBnW0obg9eUll6J8CwWnRPfsmkeMfoZxlyjmwF0p+iKRqgYuSTAGT8vLjqxcBr2FPnGkQ4U+DwiW3TwTxx6d1CBJqRe1W2qEnVhdwO509ty+9SiYiipQfEq7FA+POkQK6UienYbEhjYBkaIVZ/Xgi6z4hoYA2i35y8U5ByPt5KbxBAlNgAAtt/7PmgsKE4C3wwMVME2Uu35aBaWX2jklwh17gWRMciHQ0WVkZIRKZDiyK/szZxGbflIA5hp/p9U+yhkwJlkb2AWCnIRWBmp4nMD0lR1rU2SnJhaIUva6aMxoo= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com
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
