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

echo "$PROJECT: Preparing to sync cost totals for all submitted applications with the ifs-finance-data-service"

function financeDataServiceSync() {
    until oc create -f $(getBuildLocation)/finance-data-service-sync/92-finance-data-service-sync.yml ${SVC_ACCOUNT_CLAUSE}
    do
      oc delete -f $(getBuildLocation)/finance-data-service-sync/92-finance-data-service-sync.yml ${SVC_ACCOUNT_CLAUSE}
      sleep 10
    done
}

# Entry point


useContainerRegistry
pushFinanceDataServiceSyncImages

financeDataServiceSync

echo Waiting for container to complete
until [ "$(oc get po finance-data-service-sync ${SVC_ACCOUNT_CLAUSE} &> /dev/null; echo $?)" == 0 ] && [ "$(oc get po finance-data-service-sync -o go-template --template '{{.status.phase}}' ${SVC_ACCOUNT_CLAUSE})" == 'Succeeded' ]
do
  echo -n .
  sleep 5
done

oc logs -f finance-data-service-sync ${SVC_ACCOUNT_CLAUSE}

# tidy up the pod afterwards
oc delete pod finance-data-service-sync ${SVC_ACCOUNT_CLAUSE}
exit 0
