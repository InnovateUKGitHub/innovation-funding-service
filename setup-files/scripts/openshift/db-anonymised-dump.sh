#!/bin/bash

set -e

# TODO DW - remove debug flag
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

echo "Taking an anonymous MySQL Dump of the $PROJECT OpenShift project's database"

function startupMysqlDumpPod() {

    echo "Starting up a new db-anonymised-data pod."

    until oc create -f os-files-tmp/db-anonymised-data/67-db-anonymised-data.yml ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    do
      echo "Shutting down any pre-existing db-anonymous-data pods before starting a new one."
      oc delete -f os-files-tmp/db-anonymised-data/67-db-anonymised-data.yml ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
      sleep 10
    done
}

function waitForMysqlDumpPodToStart() {

    until oc logs db-anonymised-data ${SVC_ACCOUNT_CLAUSE} | grep "Standard MySQL Monitor" &> /dev/null;
    do
      echo "Allowing proxysql time to start up..."
      sleep 2
    done
}

function takeMysqlDump() {

    echo "Taking anonymised data dump..."

    podname=$(oc get pods ${SVC_ACCOUNT_CLAUSE} | grep -m 1 db-anonymised-data | awk '{ print $1 }')
    oc rsh $podname /dump/make-mysqldump.sh > /dev/null;
    oc rsync $podname:/dump/anonymised-dump.sql.gz /tmp > /dev/null;
    echo "Anonymised data dump taken!"
}

function shutdownMysqlDumpPodAfterUse() {

    echo "Shutting down db-anonymised-data pod.  Waiting for it to stop..."

    oc delete -f os-files-tmp/db-anonymised-data/67-db-anonymised-data.yml ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    max_termination_timeout_seconds=$((120))
    time_waited_so_far=$((0))

    until ! oc get po db-anonymised-data ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    do
      if [ "$time_waited_so_far" -gt "$max_termination_timeout_seconds" ]; then
        echo "db-anonymised-data pod didn't shut down as expected"
        exit -1;
      fi
      echo "Still waiting for db-anonymised-data pod to shut down..."
      sleep 2
      time_waited_so_far=$((time_waited_so_far + 5))
    done
}

# Entry point
cleanUp
cloneConfig
tailorAppInstance

if [[ "$TARGET" == "local" ]]; then
    export DB_NAME=ifs
    export DB_USER=root
    export DB_PASS=password
    export DB_HOST=ifs-database
    export DB_PORT=3306
fi

injectDBVariables
useContainerRegistry

pushAnonymisedDatabaseDumpImages
startupMysqlDumpPod
waitForMysqlDumpPodToStart
takeMysqlDump


## TODO DW - reinstate
#shutdownMysqlDumpPodAfterUse

echo "Job complete!  Dump now available at /tmp/anonymised-dump.sql.gz"