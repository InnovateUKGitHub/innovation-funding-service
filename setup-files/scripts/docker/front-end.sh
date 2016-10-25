#!/bin/bash
set -e
BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

cd ../../../

function css() {
    cd ifs-web-service/ifs-web-core/src/main/resources/static
    gulp css
    cd -
}

function js-core() {
    cd ifs-web-service/ifs-web-core/src/main/resources/static
    gulp js
    cd -
}
function js-app() {
    cd ifs-web-service/ifs-application-service/src/main/resources/static
    gulp js
    cd -
}
function js-ass() {
    cd ifs-web-service/ifs-assessment-service/src/main/resources/static
    gulp js
    cd -
}
function js-comp-mgt() {
    cd ifs-web-service/ifs-competition-mgt-service/src/main/resources/static
    gulp js
    cd -
}
function js-ps() {
    cd ifs-web-service/ifs-project-setup-service/src/main/resources/static
    gulp js
    cd -
}

target=$1
shift

case "$target" in
    all)
        css
        js-core
        js-comp-mgt
        js-ps
        js-app
        js-ass
    ;;
    css)
        css
    ;;
    js)
        js-core
        js-comp-mgt
        js-ps
        js-app
        js-ass
    ;;
    js-core)
        js-core
    ;;
    js-ps)
        js-ps
    ;;
    js-app)
        js-app
    ;;
    js-comp-mgt)
        js-comp-mgt
    ;;
    js-ass)
        js-ass
    ;;
    *)
        echo $"Usage: $0 {all|css|js|js-core|js-ps|js-comp-mgt|js-app|js-ass}"
        exit 1
esac
