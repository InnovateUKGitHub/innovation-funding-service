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

function getDatapodName {
 oc get pods -o go-template --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}' \
 | grep data-service \
 | head -n1
}

function copyDataToDatapod() {
  DATA_POD_NAME=$1
  oc rsh $DATA_POD_NAME mkdir -p /tmp/anonymised
  oc rsync /tmp/anonymised $DATA_POD_NAME:/tmp/
}


oc project "rp-silstub" # TODO

DATA_POD_NAME=$(getDatapodName)
copyDataToDatapod $DATA_POD_NAME

oc rsh $DATA_POD_NAME gpg --decrypt --passphrase $DB_DUMP_PASS /tmp/anonymised/anonymised-dump.sql.gpg \
| mysql -u$DB_DESTINATION_USER -p$DB_DESTINATION_PASS -h$DB_DESTINATION_HOST $DB_DESTINATION_NAME






