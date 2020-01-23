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

echo "Applying secrets for $PROJECT Openshift project"

function applySecrets() {
    ENV="QQRP"
    oc create secret generic idp-keys-secrets \
    --from-literal="$(getKeyValue ldap-encryption.crt /CI/IFS/$ENV/LDAP/ENCRYPTION/CERT)" \
    --from-literal="$(getKeyValue idp-encryption.key /CI/IFS/$ENV/IDP/ENCRYPTION/KEY)" \
    --from-literal="$(getKeyValue idp-encryption.crt /CI/IFS/$ENV/IDP/ENCRYPTION/CERT)" \
    --from-literal="$(getKeyValue idp_proxy_key.pem /CI/IFS/$ENV/IDP/PROXY/KEY)" \
    --from-literal="$(getKeyValue idp_proxy_certificate.pem /CI/IFS/$ENV/IDP/PROXY/CERT)" \
    --from-literal="$(getKeyValue idp_proxy_cacertificate.pem /CI/IFS/$ENV/IDP/PROXY/CACERT/1)" \
    --from-literal="$(getKeyValue idp-signing.key /CI/IFS/$ENV/IDP/SIGNING/KEY)" \
    --from-literal="$(getKeyValue idp-signing.crt /CI/IFS/$ENV/IDP/SIGNING/CERT)" \
    --from-literal="$(getKeyValue sp_proxy_certificate.pem /CI/IFS/$ENV/SP/PROXY/CERT)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}
}

function getKeyValue() {
    KEY=$1
    LOOKUP=$2
    if $(isNamedEnvironment ${TARGET}); then
        echo "TODO"
    else
        echo "$KEY=$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/$KEY)"
    fi
}

applySecrets