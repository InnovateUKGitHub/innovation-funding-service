#!/bin/bash

eval $(docker-machine env default)

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

cd ../../../

data() {
    cd ifs-data-service
    ./gradlew -Pprofile=docker cleanDeploy "$@"
    echo "copying data service war to container"
    for item in $( docker-compose -p ifs ps -q data  ); do
        docker cp build/war/* ${item}:/opt/tomcat/webapps/
    done
    echo "copying complete"
    echo
    echo
    cd ..
}

web() {
    cd ifs-web-service
    ./gradlew -Pprofile=docker cleanDeploy "$@"
    items=$(docker-compose -p ifs ps -q web)
    echo "copying competition management service war to containers"
    for item in $items; do
        docker cp ifs-competition-mgt-service/build/war/* ${item}:/opt/tomcat/webapps/
    done
    echo "copying application service war to containers"
    for item in $items; do
        docker cp ifs-application-service/build/war/* ${item}:/opt/tomcat/webapps/
    done
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