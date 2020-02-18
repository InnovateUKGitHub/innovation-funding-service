#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3

# Common functions
. $(dirname $0)/deploy-functions.sh

PROJECT=$(getProjectName ${PROJECT} ${TARGET})
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause ${TARGET} ${PROJECT} ${SVC_ACCOUNT_TOKEN})

echo "Applying secrets for $PROJECT Openshift project"
echo "PROJECT="${PROJECT}
echo "TARGET="${TARGET}
echo "VERSION="${VERSION}

# Apply the certs from the file system
function applyFileCerts() {
    # idp secrets
    oc create secret generic idp-keys-secrets \
        --from-literal="ldap-encryption.crt=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/ldap-encryption.crt)" \
        --from-literal="idp-encryption.key=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key)" \
        --from-literal="idp-encryption.crt=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt)" \
        --from-literal="idp_proxy_key.pem=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem)" \
        --from-literal="idp_proxy_certificate.pem=""$(cat  ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem)" \
        --from-literal="idp_proxy_cacertificate.pem=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem)" \
        --from-literal="idp-signing.key=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key)" \
        --from-literal="idp-signing.crt=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt)" \
        --from-literal="sp_proxy_certificate.pem=""$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}

    # sp secrets
    oc create secret generic sp-keys-secrets \
        --from-literal="sp_proxy_key.pem=""$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem)" \
        --from-literal="sp_proxy_certificate.pem=""$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem)" \
        --from-literal="sp_proxy_cacertificate.pem=""$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem)" \
        --from-literal="idp-signing.crt=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt)" \
        --from-literal="idp-encryption.crt=""$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}

    # ldap secrets
    oc create secret generic ldap-keys-secrets \
        --from-literal="ldap-encryption.crt=""$(cat ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt)" \
        --from-literal="ldap-encryption.key=""$(cat ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}

    # registration secrets
    oc create secret generic registration-keys-secrets \
        --from-literal="ldap-encryption.crt=""$(cat ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}
}

applyFileCerts