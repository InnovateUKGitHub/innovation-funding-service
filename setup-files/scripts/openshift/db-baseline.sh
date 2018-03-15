#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

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

function dbBaseline() {
    until oc create -f $(getBuildLocation)/db-baseline/66-dbbaseline.yml ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete -f $(getBuildLocation)/db-baseline/66-dbbaseline.yml ${SVC_ACCOUNT_CLAUSE}
      sleep 10
    done
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
fi

injectDBVariables
injectFlywayVariables

useContainerRegistry
pushDBBaselineImages

dbBaseline

echo Waiting for container to start
until [[ "$(oc get po dbbaseline ${SVC_ACCOUNT_CLAUSE} &> /dev/null; echo $?)" == 0 ]] && [[ "$(oc get po dbbaseline -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" == 'Running' || "$(oc get po dbbaseline -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" == 'Succeeded' ]]
do
  echo -n .
  sleep 5
done

oc logs -f dbbaseline ${SVC_ACCOUNT_CLAUSE}

echo Waiting for container to terminate before checking its status
sleep 5

if [[ "$(oc get po dbbaseline -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" != "Succeeded" ]]; then exit -1; fi

# tidy up the pod afterwards
oc delete pod dbbaseline ${SVC_ACCOUNT_CLAUSE}
exit 0
