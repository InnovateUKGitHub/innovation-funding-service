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
    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAy0wggMpBgkqhkiG9w0BBwagggMaMIIDFgIBADCCAw8GCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMsdXWMzq1KrjhFJ1oAgEQgIIC4KrezKu4jPcEQONSl+e9AzPW0nNhagp4zdPTmC34x4fmpCV8bU3Gv8RcypaMM+EH9HKlQprXsFDKpQIc+jgsE5tAGzTYC9lwmgeEM4605Xjulc6vz3aXEVTw3uqLJtxavKbLamZj/D7Y95G+tiIzk9MC3yiWXUxeGXA1O5tQrWqjdh6HPYdybYeCCXy7QJ6jzUFXRo+cK0aa+5w+LB9MImotRP+Lzem61kiBQiHVn/8WiDWX2wd9fT0cglVc5yzlfrZ9f3ihCISg+RjmUXWrejPH7ewcBWEFsmTlfJ4PDNFgcxe4PaiL6KUmcC7YlHG6tUHRXp1NTMby4nx+3ngRA7r+djiHdbCLhxRyUskrlZHwrwcCgM1zdt8NMke9RdG3VugC5vdef5GAg4thxR6/F/TUdWPTU6LfgyRWPJrhMcFIszag+H/qWBpML0bh/SHzlxHMimfA92Zv7dFxMoh4H5VD9A+1WmBog0qq74AZiTYrvBKbSVBL+7z+IbTDH/06zsbbPelGiOoWF2qmeK5f52R4xpJtQGKhvzCf7YJWwd0EM1I8ukF9uG2vAQU0iZprB2CKVjr6VPFvOQ7puOFGTpmXAD9We5VaZ0a8ev6LYgXh4LAKGdgdqyj51k+I46l3FVeW0a/s15m5Ph7i44ey9Zu6GXfvvjlRhXhkhd63OxulkunjdRtJyX/pDYLxrtLgDeDm7Ibi1NTu5G01eIPL8pjKyj4n7BzImV+ESUWmHnR8HL799Sk1WrMOIKM6rd+rTrMMn6t4mRpAIIpcdGKamihVfBGUT/57Bd07Ay/lThnMSbjA4nf8s6ZCTCsfBHf62tQfCTSieV/iH5MgSJKXxiU6sVs5Tja9fjmOJENBtI/wjaw7ykJ43QIDyISInenBV2FReenL7M2Q9n2pDBNGs1uJbomFrGkycUr6AtxWHjGLKZg+YUIKZI0gas1P8u/lyxV+15npKJ/zgNzucjD+M+k= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com
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
