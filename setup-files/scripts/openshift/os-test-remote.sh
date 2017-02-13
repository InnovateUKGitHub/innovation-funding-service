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
    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAy0wggMpBgkqhkiG9w0BBwagggMaMIIDFgIBADCCAw8GCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQM3ZeSRp5ovr7fIuAaAgEQgIIC4DKOFXyew9JSqSK+0S7HTN5VcXXZj0x8lViDcuNdmRJwl4CadTIQxNIobic1syGNIF/+TxTJNj4E3LlfYkpxxwNT3yzug1uP3nnc65xn5bZ+VrVHtSnljYEBIT7vu7MuS8ibseCAFVuQrtiPYZebc5sx6ua+EGBb8auXHbA6pGYvsbu3P4dCqKsTCvOa+Ow1GkHLjaSJGFRyYzJVXL79dHap1h5+afBGgDE8ggu0j3BqVlznq28pF7uwkQY7ULsuVmouVM5weAvgY7uXnYWraV/7I/yIZJdi2Na9hOHB2mIADGzrTozlurIW1bJouZdExol5RFuhtDZMf+dUCWTG2EfP7wehB3hvl2Uvz7ay8Z2CTAJf5d5svmffeJa+Y5thzgDkVeP2UjxKQqBG7YRN1VxBo8EUv+VvXoKyia2c3tIFD2eX7reQ1B9gpLg/Fg8HlE90bVN/pemHVkcZLfBYYc2scx1YH9a8bz/ZRZ6KsXaZ0bRWQ0gbPmPBDZbsUYpJ2RgSF7ahhB8Wc9JswL96ssKqvnh2SoRNjVdG+j7SxmFyaY1kJkY49sfyjouj88PYwf54LDltsDeOF1ui8OBPIbUEfprLW0Ks2NmmWQ+SermWwqGVsJBQY87tG+cqNRbQFXwF3LLc2+imth4U9mceiYBzJeoH8iRq+QGNZW9p1bsYZjIDAWsA4LoIrnAGgXtpNDUxmNk+0s8eOzm6sb5h3ojxq5S7DM/suIpmwQdWcnZ2JdD6zhjYPTaaLbF/nhPWBIOvDhrHuYn5Urej7ucKMXc+C+SatI3sDghHSUTyzvfSrrn6QXUH3YmXEOC0EpYfA3ysG9+UvUVyhuvrIbZCzBtMeE2E6ZLaLb2HgO3GX4TP/wyU4GUD9DDOoSWzOrvaGeDuA2Wbqch4p/t3Igzwo9c6PC7hZxvzjl4ASC7wWvNjBcvhw99Iky5jaNCauCcoN+PTF//DND2eKycS8hL+bDk= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com
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
