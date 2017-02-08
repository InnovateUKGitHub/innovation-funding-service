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
    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAyEwggMdBgkqhkiG9w0BBwagggMOMIIDCgIBADCCAwMGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMIGgO965/W8vj5hXcAgEQgIIC1OEsFyGL2KFUORcIRnj96utwEPxfUZXqm00BgTX5l4G0fQCb2FL0Ql7TVkQNiK44qBw38bgfiCaZVHt4sriyD5KaY5V3XKQc2X7HuglXxrmdTEgko7gYbfCrPvF3D2gNSLjAsg/tDQvJR9UsBQCZQOlSShVB5iB+pH9NEpTEFQjbq9Qd93sc9/zd4NyFgxPskEyC6ZBVl1fpTKHfCG7ugP4Uw0omkCRxxRBlH4bajD3YmtjqIN3AOoLk/8mrVbt6MROPGthadCLiBxoUJobM/1cVL5O00HZreB6fQwYdKnJMjmSCgTh2/MaRGMjdwO7iNxWBA03HrSxm/KMO4GWNkulKIkIJBC1yEdaQEt9dgP1uzEIRVFU637p3zCmJEx4vwfd0bmTBd6q0dluFdaenfPQI/Y1zovkyixX6yW39/nGpFQsErlIVWqrdeLepqJzXktAJfYbHd5GERPdt6HhH7PPA7ANrvK0/EzyH387lG66k3E2htvm238U+C5ocfE2wLdK+IQhIjrX3bKef4xonxaiFTd+m+ru265CxDkmxPXccwEwtqVbjoeguxhouBBqel1yxB6ZrFsJJbDtQB74jdJXn0dUMAVsFmX4KcdNTsTWypqp3BDd3jDHVRkkcyFYvs0adWHS0G9wZxre4Fx8zepsyhy16IhCUwUgs1h4zHNFqlRdcXSOlrXsmu7QwuNwV5p40/vmU1H5dOpane0EdTljwZT3r7h938UxcE5ojVOajg/NYDaNN70bhh/W3KU0AMEXsJspAadR/p92DLvfHyaOEkciAaULU14B4PRWpN83FNWWzAnn1z9TI6eZwdEe/S8aBQrmrliPEbzqjReQZO/2lprLYttT1CYlEqrQJ65Ilaa1P4zyRVxaAtrjIl6aC4IxfFEyvvmqppEoMt0cQ8P/NveiFHrp7jRM+yISU1o4oK0IbPDHPGFFDahtCWUO15+VUCQs= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com
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
