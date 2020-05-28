#!/bin/bash

set -e

PROJECT=$1
TARGET=$2

# Common functions
. $(dirname $0)/deploy-functions.sh

PROJECT=$(getProjectName ${PROJECT} ${TARGET})
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause ${TARGET} ${PROJECT} ${SVC_ACCOUNT_TOKEN})

# Check creating environment properties for application or build
if $(isForBuildProperties $TARGET); then
      oc ${SVC_ACCOUNT_CLAUSE} get secret properties -o jsonpath='{.data.properties}' | base64 --decode > 'gradle-support/build-env-properties.gradle'
else
  if $(isNamedEnvironment $PROJECT); then
      oc ${SVC_ACCOUNT_CLAUSE} get secret properties -o jsonpath='{.data.properties}' | base64 --decode > gradle-support/$PROJECT-named-env-properties.gradle
  else
      oc ${SVC_ACCOUNT_CLAUSE} get secret properties -o jsonpath='{.data.properties}' | base64 --decode > 'gradle-support/non-named-env-properties.gradle'
  fi
fi

