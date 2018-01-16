#!/bin/bash
echo "############################################################"
echo "# Expects there to be an anonymised database dump at:      #"
echo "# /tmp/anonymised/anonymised-dump.sql.gpg                  #"
echo "# Which should be there if db-anonymised dump has been run #"
echo "# TODO other assumptions                                   #"
echo "############################################################"
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

function checkVariables() {
    if [ -z "$DB_USER" ];                                    then echo "Set DB_USER environment variable"; exit -1; fi
    if [ -z "$DB_PASS" ];                                    then echo "Set DB_PASS environment variable"; exit -1; fi
    if [ -z "$DB_NAME" ];                                    then echo "Set DB_NAME environment variable"; exit -1; fi
    if [ -z "$DB_HOST" ];                                    then echo "Set DB_HOST environment variable"; exit -1; fi
    if [ -z "$DB_DESTINATION_SYSTEM_USER_UID" ];             then echo "Set DB_DESTINATION_SYSTEM_USER_UID environment variable"; exit -1; fi
    if [ -z "$DB_DESTINATION_SYSTEM_MAINTENANCE_USER_UID" ]; then echo "DB_DESTINATION_SYSTEM_MAINTENANCE_USER_UID environment variable"; exit -1; fi
    if [ -z "$DUMP_PASS" ];                                  then echo "Set DB_DUMP_PASS environment variable"; exit -1; fi

    DB_PORT=${DB_DESTINATION_PORT:-3306}
}

function startupMysqlClientPod() {

    echo "Starting up a new mysql-client pod."

    until oc create -f $(getBuildLocation)/mysql-client/91-mysql-client.yml ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    do
      echo "Shutting down any pre-existing mysql-client pods before starting a new one."
      oc delete -f $(getBuildLocation)/mysql-client/91-mysql-client.yml ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
      sleep 10
    done
}

function waitForMysqlClientPodToStart() {

    until oc get pods mysql-client ${SVC_ACCOUNT_CLAUSE} | grep "Running" &> /dev/null;
    do
      echo "Wait for mysql to start running"
      sleep 2
    done
}

function shutdownMysqlClientPodAfterUse() {

    echo "Shutting down mysql-client pod.  Waiting for it to stop..."

    oc delete -f $(getBuildLocation)/mysql-client/91-mysql-client.yml ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    max_termination_timeout_seconds=$((120))
    time_waited_so_far=$((0))

    until ! oc get po mysql-client ${SVC_ACCOUNT_CLAUSE} &> /dev/null;
    do
      if [ "$time_waited_so_far" -gt "$max_termination_timeout_seconds" ]; then
        echo "mysql-client pod didn't shut down as expected"
        exit -1;
      fi
      echo "Still waiting for mysql-client pod to shut down..."
      sleep 2
      time_waited_so_far=$((time_waited_so_far + 5))
    done
}

function copyDumpToMysqlClientPod() {
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client mkdir -p /tmp/anonymised
  oc rsync ${SVC_ACCOUNT_CLAUSE} /tmp/anonymised mysql-client:/tmp/
}

function insertDataIntoDestinationDatabase() {
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client \
  sh -c "gpg --decrypt --passphrase $DB_DUMP_PASS /tmp/anonymised/anonymised-dump.sql.gpg \
  | mysql -u$DB_USER -p$DB_PASS -h$DB_HOST -P$DB_PORT $DB_NAME"
}

function resetSystemUserUids() {
  MYSQL_CLIENT_POD_NAME=$1
  echo
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client \
  mysql -u$DB_DESTINATION_USER -p$DB_DESTINATION_PASS -h$DB_DESTINATION_HOST -P$DB_DESTINATION_PORT $DB_DESTINATION_NAME \
  --execute "UPDATE user SET uid = '$DB_DESTINATION_SYSTEM_USER_UID' WHERE email = 'ifs_web_user@innovateuk.org'"
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client \
  mysql -u$DB_DESTINATION_USER -p$DB_DESTINATION_PASS -h$DB_DESTINATION_HOST -P$DB_DESTINATION_PORT $DB_DESTINATION_NAME \
  --execute "UPDATE user SET uid = '$DB_DESTINATION_SYSTEM_MAINTENANCE_USER_UID' WHERE email = 'ifs_system_maintenance_user@innovateuk.org'"
}

function deleteDumpFromDataPod() {
  MYSQL_CLIENT_POD_NAME=$1
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client rm /tmp/anonymised/anonymised-dump.sql.gpg
}

checkVariables
useContainerRegistry
startupMysqlClientPod
waitForMysqlClientPodToStart
copyDumpToMysqlClientPod
insertDataIntoDestinationDatabase
resetSystemUserUids
#shutdownMysqlClientPodAfterUse