#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

if [[ "$TARGET" == "production" ]]; then
    echo "Cannot reset the database on production"
    exit 1
fi

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
HOST=$(getHost $TARGET)
ROUTE_DOMAIN=$(getRouteDomain $TARGET $HOST)
REGISTRY=$(getRegistry)
INTERNAL_REGISTRY=$(getInternalRegistry)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)
REGISTRY_TOKEN=$SVC_ACCOUNT_TOKEN

echo "Resetting the $PROJECT Openshift project"

function createDBReset(){
    # Create dbreset
    oc create -f $(getBuildLocation)/db-reset/66-dbreset.yml ${SVC_ACCOUNT_CLAUSE}
}

function waitForDBResetToStart() {
    echo Waiting for container to start
    until [[ "$(oc get po dbreset ${SVC_ACCOUNT_CLAUSE} &> /dev/null; echo $?)" == 0 ]] && [[ "$(oc get po dbreset -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" == 'Running' || "$(oc get po dbreset -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" == 'Succeeded' ]]
    do
      echo -n .
      sleep 5
    done
    oc logs -f dbreset ${SVC_ACCOUNT_CLAUSE}
}

function clearFS() {
    # Note: We remove just contents of virus-scan-holding and not the directory itself as its monitored by clamAV for scanning, but we delete other directories completely.
    echo Clearing file system directories
    oc rsh ${SVC_ACCOUNT_CLAUSE} $(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep -m 1 data-service | awk '{ print $1 }') /bin/bash -c 'cd /mnt/ifs_storage && rm -rf virus-scan-holding/* && ls | grep -v .trashcan | grep -v virus-scan-holding | xargs rm -rf'
}

function waitForTermAndCheckStatus {
    echo Waiting for container to terminate before checking its status
    sleep 5

    if [[ "$(oc get po dbreset -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" != "Succeeded" ]]; then exit -1; fi
}

function tidyUp() {
    # tidy up the pod afterwards
    echo Deleting dbreset
    oc delete pod dbreset ${SVC_ACCOUNT_CLAUSE}
}

function dbReset() {
    createDBReset
    clearFS
    waitForDBResetToStart
    waitForTermAndCheckStatus
    tidyUp
}

# Entry point

if [[ "$TARGET" == "local" || "$TARGET" == "remote" ]]; then

    export DB_NAME=ifs
    export DB_USER=root
    export DB_PASS=password
    export DB_HOST=ifs-database
    export DB_PORT=3306

    export LDAP_HOST="ldap"
    export LDAP_PORT=389
    export LDAP_PASS="default"
    export LDAP_DOMAIN="dc=nodomain"
    export LDAP_SCHEME="ldaps"

    export FLYWAY_LOCATIONS="filesystem:/flyway/sql/db/migration,filesystem:/flyway/sql/db/reference,filesystem:/flyway/sql/db/setup,filesystem:/flyway/sql/db/webtest"
    export SYSTEM_USER_UUID="c0d02979-e66e-11e7-ac43-0242ac120002"
fi

injectDBVariables
injectLDAPVariables
injectFlywayVariables

useContainerRegistry
pushDBResetImages

dbReset

exit 0