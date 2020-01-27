#!/bin/bash

set -e

PROJECT=$1
TARGET=$2
VERSION=$3
AWS_PROFILE=$4 # Name of aws profile that identifies credentials and config
AWS_ACCESS_KEY=$5 # Secret key that allows access to AWS parameter store
AWS_ACCESS_KEY_ID=$6 # Secret key id that allows access to AWS parameter store

# Common functions
. $(dirname $0)/deploy-functions.sh

PROJECT=$(getProjectName ${PROJECT} ${TARGET})
SVC_ACCOUNT_TOKEN=$(getSvcAccountToken)
SVC_ACCOUNT_CLAUSE=$(getSvcAccountClause ${TARGET} ${PROJECT} ${SVC_ACCOUNT_TOKEN})

echo "Applying secrets for $PROJECT Openshift project"
echo "PROJECT="${PROJECT}
echo "TARGET="${TARGET}
echo "VERSION="${VERSION}

function applySecrets() {
    AWS_LOOKUP_DISCRIMINATOR=$(getAwsLookupDiscriminator)

    # idp secrets
    oc create secret generic idp-keys-secrets \
    --from-literal="$(getKeyValue ldap-encryption.crt /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/LDAP/ENCRYPTION/CERT)" \
    --from-literal="$(getKeyValue idp-encryption.key /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/ENCRYPTION/KEY)" \
    --from-literal="$(getKeyValue idp-encryption.crt /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/ENCRYPTION/CERT)" \
    --from-literal="$(getKeyValue idp_proxy_key.pem /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/PROXY/KEY)" \
    --from-literal="$(getKeyValue idp_proxy_certificate.pem /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/PROXY/CERT)" \
    --from-literal="$(getKeyValue idp_proxy_cacertificate.pem /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/PROXY/CACERT/1 /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/PROXY/CACERT/2 /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/PROXY/CACERT/3)" \
    --from-literal="$(getKeyValue idp-signing.key /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/SIGNING/KEY)" \
    --from-literal="$(getKeyValue idp-signing.crt /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/SIGNING/CERT)" \
    --from-literal="$(getKeyValue sp_proxy_certificate.pem /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/SP/PROXY/CERT)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}

    # sp secrets
    oc create secret generic sp-keys-secrets \
    --from-literal="$(getKeyValue sp_proxy_key.pem /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/SP/PROXY/KEY)" \
    --from-literal="$(getKeyValue sp_proxy_certificate.pem /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/SP/PROXY/CERT)" \
    --from-literal="$(getKeyValue sp_proxy_cacertificate.pem /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/SP/PROXY/CACERT/1 /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/SP/PROXY/CACERT/2 /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/SP/PROXY/CACERT/3)" \
    --from-literal="$(getKeyValue idp-signing.crt /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/SIGNING/CERT)" \
    --from-literal="$(getKeyValue idp-encryption.crt /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/IDP/ENCRYPTION/CERT)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}

    # ldap secrets
    oc create secret generic ldap-keys-secrets \
    --from-literal="$(getKeyValue ldap-encryption.crt /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/LDAP/ENCRYPTION/CERT)" \
    --from-literal="$(getKeyValue ldap-encryption.key /CI/IFS/${AWS_LOOKUP_DISCRIMINATOR}/LDAP/ENCRYPTION/KEY)" \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}
}

# The discriminator in the aws lookup key for a particular environment
function getAwsLookupDiscriminator(){
    if [[ ${TARGET} == "ifs-prod" ]] ; then
      echo "PROD"
    else
      echo "NON-PROD"
    fi
}

# The key and value in the form key=value for the supplied key and aws lookup
# $1 key
# $2, $3.... the lookups - multiple maybe required due to size limitations
# If this is not a named environment then we do not use aws and instead use secrets in the codebase
function getKeyValue() {
    KEY=$1
    if $(isNamedEnvironment ${PROJECT}); then # TODO is this correct?
        # For named environments we get the secrets from an aws store
        echo "$KEY="
        for i in "${@:2}"
        do
            echo "$(docker exec ssm-access-container aws ssm get-parameter --name ${AWS_LOOKUP} --with-decryption --output text --query Parameter.Value --with-decryption)"
        done
    else
        # For non named environments we use the secrets stored in the codebase
        echo "$KEY=$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/${KEY})" # TODO how do we get the correct path?
    fi
}

# If we have a named environment we need to get the secrets from or aws store.
if $(isNamedEnvironment ${PROJECT}); then # TODO is this correct
    if [[ -z ${AWS_PROFILE} || -z ${AWS_ACCESS_KEY} || -z ${AWS_ACCESS_KEY_ID} ]]; then
        echo "AWS_PROFILE, AWS_ACCESS_KEY, AWS_ACCESS_KEY_ID must be specified on named environments"
        exit 1
    fi
    # Create a file with AWS credentials which mounted to the aws-cli docker image.
    mkdir -p ifs-auth-service/aws/
    echo -e "[$AWS_PROFILE]" > ifs-auth-service/aws/credentials
    echo -e "aws_access_key_id = $AWS_ACCESS_KEY_ID" >> ifs-auth-service/aws/credentials
    echo -e "aws_secret_access_key = $AWS_ACCESS_KEY" >> ifs-auth-service/aws/credentials

    # Start a docker image that can communicate with the aws
    docker stop ssm-access-container || true
    docker image rm ssm-access-image || true
    docker build --tag="ssm-access-image" docker/aws-cli
    docker run -id --rm -e AWS_PROFILE=${AWS_PROFILE} -v $PWD/ifs-auth-service/aws:/root/.aws --name ssm-access-container ssm-access-image
fi

#applySecrets
echo ${TARGET}
echo "QQRP"
docker stop ssm-access-container || true