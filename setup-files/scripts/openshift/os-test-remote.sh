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
    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAy0wggMpBgkqhkiG9w0BBwagggMaMIIDFgIBADCCAw8GCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMBiJktO4IxAC0MtlYAgEQgIIC4D66iycDLqf1z0f5fpsSCDONvnxLWV9UWCmLxnyi0ewlS9+GNjYMJdU1/pzsjsG/m3/v+65+TvyB56/n2cYIVF1LnsuEc67Zyqt7YtBdSw89+GRXLsks0bssw17/I6aGfG/6ace2yaCzVvPGs2O38NcZhOT77wgLg4ErdFIGmEMgOUWndBSdXYXZYtnngw6HZ7tYlOGgrpuMcL461W6r5wb6VH7UbFp0W4XlIWQeHQMKfExKCF9iuv61gpDQSsU4EMMvraNEkeRRuXbT8IKm5oVsheBQeksetfhNSgtn/y7OswHzObDRPcG7XLKLKujDK+NyHbX+J9+DN6/lxHNh3hLJ/ZzAc6MEfEUR7l8v8KUgL9j2Smji2q7lw+M33C4emcNUUEaCc0Tqz0/a6v0iVaV7m1lDUSDcz9vy52GztP/24AVPJiM7GdpY9846ZkhHkm+cbZFI6zf99hsKDiHkIJ7Zl2GNClxQgjiU1mx0qmB1F90Xe+6F8xMzNsVgCzfzlXR4/BNFjbarLYwhmyp6M1/1saCAjwZbffGUHhEvtbHu5OIrqVcfRKkD6vWvfWKGlYTpLprcuA2EosNDbBH2fTii2EqYDeZ3SOTVxBfIamU2GPLl2mQxdNE9Y/qTLwagjbLP47JhiuNfBDrLqRQ1WcZtHfIN92wzB7LSaqkTzl8YaQ3aQLqFVCuLZ6t5K0m6rr3a/GmsM+HOT0Hdw7En/l4roYLWRgXJNBUBjEfNXaT7jAFYWEXwH3PaDVF4AuoP36+MnRLL2xFsMSyYId3eXt0hIEcCufR07ueK0XnAjq8LvwQ4JWGLlJ5ZQscIpMMUc4vAXrCK5uJEy+ucMwBNajmwZi4yUbfHP0EVR1/SH9pes5DtChHKK3VcHu91M+fQ08B/WDga46KJ08CNAgbGT714+15XFjkOdyvSYI3nP/6RcRHkBXRAbQeGBp8SVPpd6QM2NEP3vLrv9ohaFEm2Kyk= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com
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
