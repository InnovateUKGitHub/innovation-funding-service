#!/bin/bash
set -e

PROJECT=$1
TARGET=$2
VERSION=$3

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh
. $(dirname $0)/pod-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
HOST=$(getHost $TARGET)
ROUTE_DOMAIN=$(getRouteDomain $TARGET $HOST)
REGISTRY=$(getRegistry)
INTERNAL_REGISTRY=$(getInternalRegistry)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)
REGISTRY_TOKEN=$SVC_ACCOUNT_TOKEN

function checkVariables() {
    if [ -z "${DB_USER}" ];                                 then echo "Set DB_USER environment variable"; exit -1; fi
    if [ -z "${DB_PASS}" ];                                 then echo "Set DB_PASS environment variable"; exit -1; fi
    if [ -z "${DB_ANON_PASS}" ];                            then echo "Set DB_ANON_PASS environment variable"; exit -1; fi
    if [ -z "${DB_NAME}" ];                                 then echo "Set DB_NAME environment variable"; exit -1; fi
    if [ -z "${DB_HOST}" ];                                 then echo "Set DB_HOST environment variable"; exit -1; fi
    if [ -z "${DESTINATION_SYSTEM_USER_UID}" ];             then echo "Set DESTINATION_SYSTEM_USER_UID environment variable"; exit -1; fi
    if [ -z "${DESTINATION_SYSTEM_MAINTENANCE_USER_UID}" ]; then echo "Set DESTINATION_SYSTEM_MAINTENANCE_USER_UID environment variable"; exit -1; fi
    if [ -z "${DUMP_PASS}" ];                               then echo "Set DUMP_PASS environment variable"; exit -1; fi

    DUMP_DIR_PATH=${DUMP_DIR_PATH:-"/tmp/"}
    DUMP_DIR_NAME=${DUMP_DIR_NAME:-"anonymised"}
    DUMP_NAME=${DUMP_NAME:-"anonymised-dump.sql.gpg"}
    DB_PORT=${DB_DESTINATION_PORT:-3306}
}

function copyDumpToMysqlClientPod() {
  echo "Syncing local directory ${DUMP_DIR_PATH}/${DUMP_DIR_NAME} with directory /tmp/${DUMP_DIR_NAME} on mysql-client pod"
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client mkdir -p /tmp/${DUMP_DIR_NAME}
  oc rsync ${SVC_ACCOUNT_CLAUSE} ${DUMP_DIR_PATH}/${DUMP_DIR_NAME} mysql-client:/tmp/
  echo "Synced local directory ${DUMP_DIR_PATH}/${DUMP_DIR_NAME} with directory /tmp/${DUMP_DIR_NAME} on mysql-client pod"
}

function importDump() {
  echo "Importing database dump"
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client \
  sh -c "gpg --decrypt --passphrase ${DUMP_PASS} /tmp/${DUMP_DIR_NAME}/${DUMP_NAME} \
  | mysql -u${DB_USER} -p${DB_PASS} -h${DB_HOST} -P${DB_PORT} ${DB_NAME}"
  echo "Imported database dump"
}

function resetSystemUserUids() {
  echo "Resetting system user ids"
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client \
  mysql -u${DB_USER} -p${DB_PASS} -h${DB_HOST} -P${DB_PORT} $DB_NAME \
  --execute "UPDATE user SET uid = '${DESTINATION_SYSTEM_USER_UID}' WHERE email = 'ifs_web_user@innovateuk.org'"
  oc rsh ${SVC_ACCOUNT_CLAUSE} mysql-client \
  mysql -u${DB_USER} -p${DB_PASS} -h${DB_HOST} -P${DB_PORT} ${DB_NAME} \
  --execute "UPDATE user SET uid = '${DESTINATION_SYSTEM_MAINTENANCE_USER_UID}' WHERE email = 'ifs_system_maintenance_user@innovateuk.org'"
  echo "System user ids reset"
}

if [[ "$TARGET" == "local" || "$TARGET" == "remote" ]]; then
    export DB_NAME=ifs
    export DB_USER=root
    export DB_PASS=password
    export DB_HOST=ifs-database
    export DB_PORT=3306
fi

checkVariables
useContainerRegistry
startupPod "/mysql-client/91-mysql-client.yml" ${SVC_ACCOUNT_CLAUSE}
waitForPodToStart "mysql-client" ${SVC_ACCOUNT_CLAUSE}
copyDumpToMysqlClientPod
importDump
resetSystemUserUids
deletePod "mysql-client" "/mysql-client/91-mysql-client.yml" ${SVC_ACCOUNT_CLAUSE}