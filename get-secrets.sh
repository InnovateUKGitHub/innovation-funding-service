#!/bin/bash

if [[ $# != 4 ]] ; then
  echo "Called with wrong arguments. Script requires ENV, AWS_PROFILE, KEY_ID, KEY"
  exit 0
fi

#Must be either PROD (for running on production environment), or NON-PROD (for running on any other named environment)
if [[ "$1" == "ifs-prod" ]] ; then
  ENV="PROD"
else
  ENV="NON-PROD"
fi

echo "Environment: $ENV"

#Name of aws profile that identifies credentials and config
AWS_PROFILE=$2
#Secret id that allows access to AWS parameter store
KEY_ID=$3
#Secret key that allows access to AWS parameter store
KEY=$4

# $1 = paramName
getParameter () {
  docker exec ssm-access-container aws ssm get-parameter --name $1 --with-decryption | jq ".Parameter.Value" | tr -d \"
}

# $1 = paramValue, $2 = writePath
writeParameter () {
  echo -e "$1" > "$2"
}

#SSM Parameter store can store up to 4k characters, therefore longer secrets are split when stored and reassembled here
# $1 = paramValue $2 = writePath
appendParameter () {
  echo -e "$1" >> "$2"
}

#aggregate function $1 = paramValue, $2 = writePath
getThenWriteParameter () {
  param= getParameter "$1"
  writeParameter $param "$2"
}

#aggregate function $1 = paramValue, $2 = writePath
getThenAppendParameter () {
  param= getParameter "$1"
  appendParameter "$param" "$2"
}

mkdir ifs-auth-service/aws/
touch ifs-auth-service/aws/credentials
mkdir -p ifs-auth-service/ifs-ldap-service/src/main/docker/certs/
mkdir -p ifs-auth-service/ifs-idp-service/src/main/docker/certs/
mkdir -p ifs-auth-service/ifs-sp-service/src/main/docker/certs/

echo -e "[$AWS_PROFILE]
aws_access_key_id = $KEY_ID
aws_secret_access_key = $KEY" > ifs-auth-service/aws/credentials

docker image rm ssm-access-image
docker build --tag="ssm-access-image" docker/aws-cli
docker run -id --rm -e AWS_PROFILE=$AWS_PROFILE -v $PWD/ifs-auth-service/aws:/root/.aws --name ssm-access-container ssm-access-image

getThenWriteParameter "/CI/IFS/$ENV/LDAP/ENCRYPTION/KEY" "ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key"
getThenWriteParameter "/CI/IFS/$ENV/LDAP/ENCRYPTION/CERT" "ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt"
getThenWriteParameter "/CI/IFS/$ENV/IDP/ENCRYPTION/KEY" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key"
getThenWriteParameter "/CI/IFS/$ENV/IDP/ENCRYPTION/CERT" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt"
getThenWriteParameter "/CI/IFS/$ENV/IDP/PROXY/KEY" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem"
getThenWriteParameter "/CI/IFS/$ENV/IDP/PROXY/CERT" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem"
getThenWriteParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/1" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"
getThenWriteParameter "/CI/IFS/$ENV/IDP/SIGNING/KEY" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key"
getThenWriteParameter "/CI/IFS/$ENV/IDP/SIGNING/CERT" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt"
getThenWriteParameter "/CI/IFS/$ENV/SP/PROXY/KEY" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem"
getThenWriteParameter "/CI/IFS/$ENV/SP/PROXY/CERT" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem"
getThenWriteParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/1" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"

if [ "$ENV" == "NON-PROD" ]; then
  getThenAppendParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/2" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"
  getThenAppendParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/3" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"
  getThenAppendParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/2" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"
  getThenAppendParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/3" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"
fi

docker stop ssm-access-container
