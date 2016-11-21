#!/bin/bash

function showUsage() {
    echo "No arguments provided."
    echo
    echo $"Usage:"
    echo
    echo "$0 {all|data|web|asm|comp-mgt|app|ps|psm} {gradleOpts}"
    echo
}

set -e

BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd ${BASEDIR}

cd ../../../

if [ -z "$1" ]
then
    showUsage
    exit 1
fi

# Runs a gradle cleanDeploy task and then copy the built .war over
# to the respective Docker container.
function deploy() {
    base="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

    service=$1
    dir=$2
    name=$(basename ${dir})

    shift 2

    cd ${dir}
    echo `pwd`
    ../gradlew -Pprofile=docker cleanDeploy "$@"

    echo
    echo "Copying ${name} war to container..."

    for item in $( docker-compose -p ifs ps -q ${service}  ); do
        docker cp build/war/* ${item}:/opt/tomcat/webapps/
    done

    echo "Copy complete."
    echo
    echo
    cd ${base}
}

function core() {
    cd ifs-web-service/ifs-web-core
    ../gradlew -Pprofile=docker cleanDeploy "$@"
    cd ../..
}

function data() {
    deploy data ifs-data-service "$@"
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
        api "$@"
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
        showUsage
        exit 1
esac
