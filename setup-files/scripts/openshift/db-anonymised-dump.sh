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

function takeMysqlDump() {
    echo "Taking anonymised data dump"
    mkdir -p /tmp/anonymised
    oc rsh ${SVC_ACCOUNT_CLAUSE} db-anonymised-data /dump/make-mysqldump.sh > /dev/null;
    oc rsync ${SVC_ACCOUNT_CLAUSE} db-anonymised-data:/dump/anonymised-dump.sql.gpg /tmp/anonymised/ > /dev/null;
    echo "Anonymised data dump taken"
}

if [[ "$TARGET" == "local" || "$TARGET" == "remote" ]]; then
    export DB_NAME=ifs
    export DB_USER=root
    export DB_PASS=password
    export DB_HOST=ifs-database
    export DB_PORT=3306
fi

injectDBVariables
useContainerRegistry
pushAnonymisedDatabaseDumpImages
startupPod "/db-anonymised-data/67-db-anonymised-data.yml" ${SVC_ACCOUNT_CLAUSE}
waitForPodLogs "db-anonymised-data" "Standard MySQL Monitor" ${SVC_ACCOUNT_CLAUSE}
takeMysqlDump
deletePod "db-anonymised-data" "/db-anonymised-data/67-db-anonymised-data.yml" ${SVC_ACCOUNT_CLAUSE}