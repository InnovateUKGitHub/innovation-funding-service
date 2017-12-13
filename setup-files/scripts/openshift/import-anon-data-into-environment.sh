#!/bin/bash
echo "############################################################"
echo "# Expects there to be an anonymised database dump at:      #"
echo "# /tmp/anonymised/anonymised-dump.sql.gpg                  #"
echo "# Which should be there if db-anonymised dump has been run #"
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
    if [ -z "$DB_DESTINATION_USER" ]; then echo "Set DB_DESTINATION_USER environment variable"; exit -1; fi
    if [ -z "$DB_DESTINATION_PASS" ]; then echo "Set DB_DESTINATION_PASS environment variable"; exit -1; fi
    if [ -z "$DB_DESTINATION_NAME" ]; then echo "Set DB_DESTINATION_NAME environment variable"; exit -1; fi
    if [ -z "$DB_DESTINATION_HOST" ]; then echo "Set DB_DESTINATION_HOST environment variable"; exit -1; fi
    if [ -z "$DB_DESTINATION_SYSTEM_USER_UID" ]; then echo "Set DB_DESTINATION_SYSTEM_USER_UID environment variable"; exit -1; fi
    if [ -z "$DB_DESTINATION_SYSTEM_MAINTENANCE_USER_UID" ]; then echo "DB_DESTINATION_SYSTEM_MAINTENANCE_USER_UID environment variable"; exit -1; fi
    if [ -z "$DB_DUMP_PASS" ]; then echo "Set DB_DUMP_PASS environment variable"; exit -1; fi
    DB_DESTINATION_PORT=${DB_DESTINATION_PORT:-3306}
}

function getDatapodName {
 oc get pods -o go-template --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}' ${SVC_ACCOUNT_CLAUSE} \
 | grep data-service \
 | head -n1
}

function copyDumpToDatapod() {
  DATA_POD_NAME=$1
  oc rsh ${SVC_ACCOUNT_CLAUSE} $DATA_POD_NAME mkdir -p /tmp/anonymised
  oc rsync ${SVC_ACCOUNT_CLAUSE} /tmp/anonymised $DATA_POD_NAME:/tmp/
}

function insertDataIntoDestinationDatabase() {
  DATA_POD_NAME=$1
  oc rsh ${SVC_ACCOUNT_CLAUSE} $DATA_POD_NAME apt-get update
  oc rsh ${SVC_ACCOUNT_CLAUSE} $DATA_POD_NAME apt-get install mysql-client -y
  oc rsh ${SVC_ACCOUNT_CLAUSE} $DATA_POD_NAME \
  bash -c "gpg --decrypt --passphrase $DB_DUMP_PASS /tmp/anonymised/anonymised-dump.sql.gpg \
  | mysql -u$DB_DESTINATION_USER -p$DB_DESTINATION_PASS -h$DB_DESTINATION_HOST -P$DB_DESTINATION_PORT $DB_DESTINATION_NAME"
}

function resetSystemUserUids() {
  DATA_POD_NAME=$1
  echo
  oc rsh ${SVC_ACCOUNT_CLAUSE} $DATA_POD_NAME \
  mysql -u$DB_DESTINATION_USER -p$DB_DESTINATION_PASS -h$DB_DESTINATION_HOST -P$DB_DESTINATION_PORT $DB_DESTINATION_NAME \
  --execute "UPDATE user SET uid = '$DB_DESTINATION_SYSTEM_USER_UID' WHERE email = 'ifs_web_user@innovateuk.org'"
  oc rsh ${SVC_ACCOUNT_CLAUSE} $DATA_POD_NAME \
  mysql -u$DB_DESTINATION_USER -p$DB_DESTINATION_PASS -h$DB_DESTINATION_HOST -P$DB_DESTINATION_PORT $DB_DESTINATION_NAME \
  --execute "UPDATE user SET uid = '$DB_DESTINATION_SYSTEM_MAINTENANCE_USER_UID' WHERE email = 'ifs_system_maintenance_user@innovateuk.org'"
}

function deleteDumpFromDataPod() {
  DATA_POD_NAME=$1
  oc rsh ${SVC_ACCOUNT_CLAUSE} $DATA_POD_NAME rm /tmp/anonymised/anonymised-dump.sql.gpg
}


checkVariables
DATA_POD_NAME=$(getDatapodName)
copyDumpToDatapod $DATA_POD_NAME
insertDataIntoDestinationDatabase $DATA_POD_NAME
resetSystemUserUids $DATA_POD_NAME
deleteDumpFromDataPod $DATA_POD_NAME