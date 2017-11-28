#!/usr/bin/env bash

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

# Entry point
createProjectIfNecessaryForNonNamedEnvs

# Need a mysql database for non-named environments. Named environments have their own.
pushApplicationImages
if ! $(isNamedEnvironment ${TARGET}); then
    pushIfsMysqlDatabase
fi

# The SIL stub is required in all environments, in one form or another, except for production.
if ! $(isProductionEnvironment ${TARGET}); then
    pushSilStubImages
fi