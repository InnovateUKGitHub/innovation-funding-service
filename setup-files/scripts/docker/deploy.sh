#!/bin/bash

eval $(docker-machine env default)

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

cd ../../../

data() {
    cd ifs-data-service
    ./gradlew -Pprofile=docker cleanDeploy "$@"
    echo "copying data service war to container"
    docker cp build/war/* ifs-data-service:/opt/tomcat/webapps/
    echo "copying complete"
    echo
    echo
    cd ..
}

web() {
    cd ifs-web-service
    ./gradlew -Pprofile=docker cleanDeploy "$@"
    echo "copying competition management service war to container"
    docker cp ifs-competition-mgt-service/build/war/* ifs-web-service:/opt/tomcat/webapps/
    echo "copying application service war to container"
    docker cp ifs-application-service/build/war/* ifs-web-service:/opt/tomcat/webapps/
    echo "copying complete"
    echo
    echo
    cd ..
}

target=$1
shift

case "$target" in
    all)
        data "$@"
        web "$@"
    ;;
    data)
        data "$@"
    ;;
    web)
        web "$@"
    ;;
    *)
        echo $"Usage: $0 {all|data|web} {gradleOpts}"
        exit 1
esac