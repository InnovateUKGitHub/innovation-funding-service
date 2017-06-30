#!/bin/bash

set -e
set -x

PROJECT=$1
TARGET=$2
VERSION=$3

. $(dirname $0)/common-functions.sh
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

echo "Taking an anonymous MySQL Dump of the $PROJECT Openshift project's database"

function dbTakeMysqlDump() {
    until oc create -f os-files-tmp/db-anonymised-data/67-db-anonymised-data.yml ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete -f os-files-tmp/db-anonymised-data/67-db-anonymised-data.yml ${SVC_ACCOUNT_CLAUSE}
      sleep 10
    done

    echo "Allowing proxysql time to start up"
    sleep 10

    podname=$(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep -m 1 db-anonymised-data | awk '{ print $1 }')
    oc rsh $podname id -g
    oc rsh $podname /dump/make-mysqldump.sh
    oc rsync $podname:/dump/dump /tmp
}

# Entry point
cleanUp
cloneConfig
tailorAppInstance

# TODO DW
export DB_NAME=ifs
export DB_USER=root
export DB_PASS=password
export DB_HOST=ifs-database
export DB_PORT=3306

injectDBVariables
useContainerRegistry

pushAnonymisedDatabaseDumpImages
dbTakeMysqlDump

echo Waiting for container to start
until [ "$(oc get po db-anonymised-data ${SVC_ACCOUNT_CLAUSE} &> /dev/null; echo $?)" == 0 ] && [ "$(oc get po db-anonymised-data -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" == 'Running' ]
do
  echo -n .
  sleep 5
done

oc logs -f db-anonymised-data ${SVC_ACCOUNT_CLAUSE}

echo Waiting for container to terminate before checking its status
sleep 5

if [[ "$(oc get po db-anonymised-data -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" != "Succeeded" ]]; then exit -1; fi

exit 0
