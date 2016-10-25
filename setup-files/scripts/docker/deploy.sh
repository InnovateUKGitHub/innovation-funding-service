#!/bin/bash
set -e

eval $(docker-machine env default)

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

cd ../../../

function deploy() {
    base="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

    service=$1
    dir=$2
    name=$(basename ${dir})

    shift 2

    cd ${dir}
    echo `pwd`
    ../gradlew -Pprofile=docker cleanDeploy "$@"
    echo "copying ${name} war to container"
    for item in $( docker-compose -p ifs ps -q ${service}  ); do
        docker cp build/war/* ${item}:/opt/tomcat/webapps/
    done
    echo "copying complete"
    echo
    echo
    cd $base
}

function core() {
    cd ifs-web-service/ifs-web-core
    ../gradlew -Pprofile=docker cleanDeploy "$@"
    cd ../..
}

function data() {
    deploy data ifs-data-service "$@"
}

function api() {
    deploy api ifs-rest-api-service "$@"
}

function asm() {
    deploy web ifs-web-service/ifs-assessment-service "$@"
}

function cmgt() {
    deploy web ifs-web-service/ifs-competition-mgt-service "$@"
}

function app() {
    deploy web ifs-web-service/ifs-application-service "$@"
}

function ps() {
    deploy web ifs-web-service/ifs-project-setup-service "$@"
}

function psm() {
    deploy web ifs-web-service/ifs-project-setup-mgt-service "$@"
}

target=$1
shift

case "$target" in
    all)
        data "$@"
        core "$@"
        app "$@"
        cmgt "$@"
        asm "$@"
        ps "$@"
        psm "$@"
    ;;
    data)
        data "$@"
    ;;
    api)
        api "$@"
    ;;
    web)
        core "$@"
        app "$@"
        cmgt "$@"
        asm "$@"
        ps "$@"
        psm "$@"
    ;;
    asm)
        core "$@"
        asm "$@"
    ;;
    comp-mgt)
        core "$@"
        cmgt "$@"
    ;;
    app)
        core "$@"
        app "$@"
    ;;
    ps)
        core "$@"
        ps "$@"
    ;;
    psm)
        core "$@"
        psm "$@"
    ;;
    *)
        echo $"Usage: $0 {all|data|web|asm|comp-mgt|app|ps|psm} {gradleOpts}"
        exit 1
esac
