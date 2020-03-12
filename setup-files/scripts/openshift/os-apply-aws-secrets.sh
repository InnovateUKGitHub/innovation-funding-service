#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3
USE_IAM=$4 # Use IAM to authenticate, instead of instead of local credentials.
: ${USE_IAM:="true"} # Default to IAM. This will work on bamboo, but not on developer machines.
LOCAL_AWS_PROFILE="iukorg" # If USE_IAM = "true" then this the profile we use for local credentials.

# Common functions
. $(dirname $0)/deploy-functions.sh

PROJECT=$(getProjectName ${PROJECT} ${TARGET})
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause ${TARGET} ${PROJECT} ${SVC_ACCOUNT_TOKEN})

echo "Applying secrets for $PROJECT Openshift project"
echo "PROJECT="${PROJECT}
echo "TARGET="${TARGET}
echo "VERSION="${VERSION}
echo "USE_IAM"=${USE_IAM}

if [[ ${USE_IAM} != "true" && ${USE_IAM} != "false" ]]; then
    echo "IF USE_IAM is specified it must be either 'true' or 'false'"
    exit 1
fi

# Apply the certs from the aws
# $1 the discriminator
# Note that we use "oc create secret" with "--dry-run" to generate the yaml. This is then applied with "oc apply".
# We do this because "oc create secret" fails if there is already a secret present, while "oc apply" will create or
# update but only takes yaml.
function applyAwsCerts() {
    # idp secrets
    oc create secret generic idp-keys-secrets \
        --from-literal="ldap-encryption.crt=""$(valueFromAws /CI/IFS/$1/LDAP/ENCRYPTION/CERT)" \
        --from-literal="idp-encryption.key=""$(valueFromAws /CI/IFS/$1/IDP/ENCRYPTION/KEY)" \
        --from-literal="idp-encryption.crt=""$(valueFromAws /CI/IFS/$1/IDP/ENCRYPTION/CERT)" \
        --from-literal="idp_proxy_key.pem=""$(valueFromAws /CI/IFS/$1/IDP/PROXY/KEY)" \
        --from-literal="idp_proxy_certificate.pem=""$(valueFromAws  /CI/IFS/$1/IDP/PROXY/CERT)" \
        --from-literal="idp_proxy_cacertificate.pem=""$(valueFromAws /CI/IFS/$1/IDP/PROXY/CACERT/1 /CI/IFS/$1/IDP/PROXY/CACERT/2 /CI/IFS/$1/IDP/PROXY/CACERT/3)" \
        --from-literal="idp-signing.key=""$(valueFromAws /CI/IFS/$1/IDP/SIGNING/KEY)" \
        --from-literal="idp-signing.crt=""$(valueFromAws /CI/IFS/$1/IDP/SIGNING/CERT)" \
        --from-literal="sp_proxy_certificate.pem=""$(valueFromAws /CI/IFS/$1/SP/PROXY/CERT)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}

    # sp secrets
    oc create secret generic sp-keys-secrets \
        --from-literal="sp_proxy_key.pem=""$(valueFromAws /CI/IFS/$1/SP/PROXY/KEY)" \
        --from-literal="sp_proxy_certificate.pem=""$(valueFromAws /CI/IFS/$1/SP/PROXY/CERT)" \
        --from-literal="sp_proxy_cacertificate.pem=""$(valueFromAws /CI/IFS/$1/SP/PROXY/CACERT/1 /CI/IFS/$1/SP/PROXY/CACERT/2 /CI/IFS/$1/SP/PROXY/CACERT/3)" \
        --from-literal="idp-signing.crt=""$(valueFromAws /CI/IFS/$1/IDP/SIGNING/CERT)" \
        --from-literal="idp-encryption.crt=""$(valueFromAws /CI/IFS/$1/IDP/ENCRYPTION/CERT)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}

    # ldap secrets
    oc create secret generic ldap-keys-secrets \
        --from-literal="ldap-encryption.crt=""$(valueFromAws /CI/IFS/$1/LDAP/ENCRYPTION/CERT)" \
        --from-literal="ldap-encryption.key=""$(valueFromAws /CI/IFS/$1/LDAP/ENCRYPTION/KEY)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}

    # registration secrets
    oc create secret generic registration-keys-secrets \
        --from-literal="ldap-encryption.crt=""$(valueFromAws /CI/IFS/$1/LDAP/ENCRYPTION/CERT)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}
}

# The value for the supplied key and aws lookups
# $1 $2, $3.... the lookups - multiple maybe required due to size limitations on what can be stored in AWS
function valueFromAws() {
    for AWS_LOOKUP in "$@"
    do
        echo "$(docker exec ssm-access-container aws ssm get-parameter --name ${AWS_LOOKUP} --with-decryption --output text --query Parameter.Value --with-decryption)"
    done
}

# Create a file with aws credentials which mounted to the aws-cli docker image.
docker stop ssm-access-container || true
docker image rm ssm-access-image || true
docker build --tag="ssm-access-image" docker/aws-cli

if [[ "$USE_IAM" = "false" ]]; then
    # Use the local developer AWS credentials as the mount point for this container
    docker run -id --rm -e AWS_PROFILE=${LOCAL_AWS_PROFILE} -v ~/.aws:/root/.aws --name ssm-access-container ssm-access-image
else
    # Authentication delegated to IAM. Will only work on AWS containers
    docker run -id --rm --name ssm-access-container ssm-access-image
fi

applyAwsCerts $([[ ${TARGET} == "ifs-prod" ]] && echo "PROD"|| echo "NON-PROD")

docker stop ssm-access-container || true