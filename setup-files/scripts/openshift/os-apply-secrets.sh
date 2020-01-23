#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3
AWS_PROFILE=$4 # Name of aws profile that identifies credentials and config
AWS_ACCESS_KEY=$5 # Secret key that allows access to AWS parameter store

# Common functions
. $(dirname $0)/deploy-functions.sh

PROJECT=$(getProjectName $PROJECT $TARGET)
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause $TARGET $PROJECT $SVC_ACCOUNT_TOKEN)

echo "Applying secrets for $PROJECT Openshift project"

function applySecrets() {
    AWS_DIFFERENTIATOR=$(getAwsDifferentiator)
    oc create secret generic idp-keys-secrets \
    --from-literal="$(getKeyValue ldap-encryption.crt /CI/IFS/$AWS_DIFFERENTIATOR/LDAP/ENCRYPTION/CERT)" \
    --from-literal="$(getKeyValue idp-encryption.key /CI/IFS/$AWS_DIFFERENTIATOR/IDP/ENCRYPTION/KEY)" \
    --from-literal="$(getKeyValue idp-encryption.crt /CI/IFS/$AWS_DIFFERENTIATOR/IDP/ENCRYPTION/CERT)" \
    --from-literal="$(getKeyValue idp_proxy_key.pem /CI/IFS/$AWS_DIFFERENTIATOR/IDP/PROXY/KEY)" \
    --from-literal="$(getKeyValue idp_proxy_certificate.pem /CI/IFS/$AWS_DIFFERENTIATOR/IDP/PROXY/CERT)" \
    --from-literal="$(getKeyValue idp_proxy_cacertificate.pem /CI/IFS/$AWS_DIFFERENTIATOR/IDP/PROXY/CACERT/1)" \
    --from-literal="$(getKeyValue idp-signing.key /CI/IFS/$AWS_DIFFERENTIATOR/IDP/SIGNING/KEY)" \
    --from-literal="$(getKeyValue idp-signing.crt /CI/IFS/$AWS_DIFFERENTIATOR/IDP/SIGNING/CERT)" \
    --from-literal="$(getKeyValue sp_proxy_certificate.pem /CI/IFS/$AWS_DIFFERENTIATOR/SP/PROXY/CERT)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}
}

# The differentiator in the aws lookup key for a particular environment
function getAwsDifferentiator(){
    if [[ $TARGET == "ifs-prod" ]] ; then
      echo "PROD"
    else
      echo "NON-PROD"
    fi
}

# The key and value in the form key=value for the supplied key and aws lookup key
# $1 key
# $2 aws lookup key
# If this is not a named environment then we do not use aws and instead use secrets in the codebase
function getKeyValue() {
    KEY=$1
    AWS_LOOKUP=$2
    if $(isNamedEnvironment ${TARGET}); then
        # For named environments we get the secrets from an aws store
        echo "$KEY=$(docker exec ssm-access-container aws ssm get-parameter --name $AWS_LOOKUP --with-decryption | jq ".Parameter.Value" | tr -d \")"
    else
        # For non named environments we use the secrets stored in the codebase
        echo "$KEY=$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/$KEY)"
    fi
}

# If we have a named environment we need to get the secrets from or aws store.
if $(isNamedEnvironment ${TARGET}); then
    if [[ -z $AWS_PROFILE || -z AWS_ACCESS_KEY ]]; then
        echo "AWS_PROFILE and AWS_ACCESS_KEY must be specified on named environments"
    fi
    # Create a file with AWS credentials which mounted to the aws-cli docker image.
    mkdir -p ifs-auth-service/aws/
    echo $AWS_ACCESS_KEY > ifs-auth-service/aws/credentials

    # Start a docker image that can communicate with the aws
    docker image rm ssm-access-image || true
    docker build --tag="ssm-access-image" docker/aws-cli
    docker run -id --rm -e AWS_PROFILE=$AWS_PROFILE -v $PWD/ifs-auth-service/aws:/root/.aws --name ssm-access-container ssm-access-image
fi

applySecrets

docker stop ssm-access-container || true