#!/usr/bin/env bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3
NEXUS_USER=$4
NEXUS_PASS=$5

. $(dirname $0)/deploy-functions.sh
. $(dirname $0)/local-deploy-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
HOST=$(getHost $TARGET)
ROUTE_DOMAIN=$(getRouteDomain $TARGET $HOST)
REGISTRY=$(getRegistry)
NEXUS_REGISTRY=$(getNexusRegistryUrl)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)
REGISTRY_TOKEN=$SVC_ACCOUNT_TOKEN

echo "Deploying the $PROJECT Openshift project"

docker login --username ${NEXUS_USER} --password ${NEXUS_PASS} ${NEXUS_REGISTRY}

docker pull docker-ifs.devops.innovateuk.org/release/sp-service:1.1.111

docker images


function deploy() {
    oc create -f $(getBuildLocation)/shib/5-shib.yml ${SVC_ACCOUNT_CLAUSE}
}

# move to deploy-functions
#function shibInit() {
#    echo "Shib init.."
#    LDAP_POD=$(oc get pods  ${SVC_ACCOUNT_CLAUSE} | awk '/ldap/ { print $1 }')
#    echo "Ldap pod: ${LDAP_POD}"
#
#     while RESULT=$(oc rsh ${SVC_ACCOUNT_CLAUSE} $LDAP_POD /usr/local/bin/ldap-sync-from-ifs-db.sh ifs-database 2>&1); echo $RESULT; echo $RESULT | grep "ERROR"; do
#        echo "Shibinit failed. Retrying.."
#        sleep 10
#    done
#}

# Entry point
if [[ ${TARGET} == "local" ]]
then
    replacePersistentFileClaim
fi

useNexusRegistry
deploy
#blockUntilServiceIsUp

#if [[ ${TARGET} == "local" || ${TARGET} == "remote" ]]
#then
#    shibInit
#fi
