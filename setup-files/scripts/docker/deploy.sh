#!/bin/bash

startingDir= pwd

case $OSTYPE in
    darwin*)
        echo "Mac detected"
        eval $(docker-machine env default)
        ;;
    linux*)
        echo "Linux detected"
        ;;
    *)
        echo "Unable to determine a supported operating system for this script.  Currently only supported on Linux and Macs"
        exit 1
        ;;
esac

cd ../../../

data() {
    ifs-data-service/gradlew cleanDeploy "$@"
    docker cp ifs-data-service/build/war/* ifs-data-service:/opt/tomcat/webapps/
}

web() {
    ifs-web-service/gradlew cleanDeploy "$@"
    docker cp ifs-web-service/ifs-competition-mgt-service/build/war/* ifs-web-service:/opt/tomcat/webapps/
    docker cp ifs-web-service/ifs-application-service/build/war/* ifs-web-service:/opt/tomcat/webapps/
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
        echo $"Usage: $0 {all|data|web}"
        exit 1
esac

cd $startingDir