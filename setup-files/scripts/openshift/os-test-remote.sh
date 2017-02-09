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
    docker login -u AWS -p AQECAHhNDzWVnYGylK3tUalHANpmrnQza5w4W4cHYMU82tbNIwAAAyEwggMdBgkqhkiG9w0BBwagggMOMIIDCgIBADCCAwMGCSqGSIb3DQEHATAeBglghkgBZQMEAS4wEQQMjyJaz2kaqyaE8gS/AgEQgIIC1Nfkvq4JevT5LT+EkRAv6ixiE1SyjUbJJj2Xlc+V5314chIYKy3jS6uqsrCUUzGpeYhHTZij3CFe6rZZEIk1jYgrYq650QJqbyCLrqE0Rh3qpE8+TMZ8Ac6U1IaRaCVurEQTeFqs5ZxXlIQ94idoFADE78ORkUlLXBGv9ewx2lZtscdXUisNA1TihyEB2vn4yE4zsCZmazBeM6JcY69uGvi+r5CL/K2McUpqEm4w/rwUD+zPeh/ycretXHAgwqgDziIf5IbRRDEBVNmgNGXWi/TJh9Ey6Zu72xwVVGYeQUdgpiRWxqAH+4/NuzxaqkJlUw593CC5AVxY/9N/hh3eEKRHOQ+9Vq55/rIQC9OvJ6jRO5IoIqPJwMWzjEPxaQWYKA3qvQJkCDrwdMFZ6mDy22t7WUwuuVhzQpyWv8GJrZoBFu8UiPJmOimf6MTWiAkmb9ivSQnqa7P+t9i4jXVrrPgouLjv+Vn9a+qYu00Dx43wcXX4acM0cZ2fwSfW5WtuUzMG2kDGxJwmZKwMZTfwCjl3sNj4I1Lss1ZRfmKvNza616Uz50q19XftiHPAVHuYkj9wFxSLavpm8UNySSl9cLM/adkRFUlCohP8fWjoIKpFlxmklY/zTn2qd2sH84dIyw5zQFJRN9AE3v9bkW39LoyME9JOr7M0dFsIi/+18zJdM0v0CE5DupSVzB0dRVg+Szpy1sSXhSci4JSlI5WxVG47sQMQ5h7EvsiD6CW3zkHlIhiQZp3i7CVNCOltiWPEE4jz3n5vjuYUMuCzFXzfFO8NygqPU3RXO76HOsSowaz1qIbmKi0FXHxW8GxESbxhvj8Y9pF028NRLqGrrZ2ARwXWZ4pB0SGUHnhkNzUwzCWraqAE83jdaMMtfoTuvwhnyEvSdg6TMd1yCqv8vx4/661gii60CSJRe5nBP/iQrz1caMvEM8pT7QN7jPmz+4dkLcRo+MA= -e none https://721685138178.dkr.ecr.eu-west-2.amazonaws.com
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
