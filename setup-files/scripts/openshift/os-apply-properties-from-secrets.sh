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

function applyProperties() {

#   Copy values to a file, this is needed  as multiline values for properties mess up if using --from-literal
    echo "$(valueFromAws)" >> "properties.gradle"

    oc create secret generic properties \
        --from-file=properties=properties.gradle \
    ${SVC_ACCOUNT_CLAUSE} --dry-run -o yaml | \
    oc apply -f - ${SVC_ACCOUNT_CLAUSE}
}

function valueFromAws() {
   echo "$(docker exec ssm-access-container aws ssm get-parameters-by-path --path /CI/IFS/$TARGET/PROPERTIES/ --no-paginate --query "Parameters[].Value" --with-decryption --output text)"
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

applyProperties

docker stop ssm-access-container || true