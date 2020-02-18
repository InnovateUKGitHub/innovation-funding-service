#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3
AWS_PROFILE=$4 # Name of aws profile that identifies credentials and config - required for named environments
AWS_ACCESS_KEY=$5 # Secret key that allows access to AWS parameter store - required for named environments
AWS_ACCESS_KEY_ID=$6 # Secret key id that allows access to AWS parameter store - required for named environments

# Common functions
. $(dirname $0)/deploy-functions.sh

PROJECT=$(getProjectName ${PROJECT} ${TARGET})
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause ${TARGET} ${PROJECT} ${SVC_ACCOUNT_TOKEN})

echo "Applying secrets for $PROJECT Openshift project"
echo "PROJECT="${PROJECT}
echo "TARGET="${TARGET}
echo "VERSION="${VERSION}
echo "AWS_PROFILE="${AWS_PROFILE}

if [[ -z ${AWS_PROFILE} || -z ${AWS_ACCESS_KEY} || -z ${AWS_ACCESS_KEY_ID} ]]; then
    echo "AWS_PROFILE, AWS_ACCESS_KEY, AWS_ACCESS_KEY_ID must be specified"
    exit 1
fi


# Apply the certs from the aws
# $1 the discriminator
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
mkdir -p ifs-auth-service/aws/
echo -e "[$AWS_PROFILE]" > ifs-auth-service/aws/credentials
echo -e "aws_access_key_id = $AWS_ACCESS_KEY_ID" >> ifs-auth-service/aws/credentials
echo -e "aws_secret_access_key = $AWS_ACCESS_KEY" >> ifs-auth-service/aws/credentials

# Start a docker image that can communicate with the aws
docker stop ssm-access-container || true
docker image rm ssm-access-image || true
docker build --tag="ssm-access-image" docker/aws-cli
docker run -id --rm -e AWS_PROFILE=${AWS_PROFILE} -v $PWD/ifs-auth-service/aws:/root/.aws --name ssm-access-container ssm-access-image

applyAwsCerts $([[ ${TARGET} == "ifs-prod" ]] && echo "PROD"|| echo "NON-PROD")

docker stop ssm-access-container || true