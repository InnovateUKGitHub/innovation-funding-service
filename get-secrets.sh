#!/bin/bash
ENV=$1
AWS_PROFILE=$2
KEY_ID=$3
KEY=$4

mkdir ifs-auth-service/aws/
touch ifs-auth-service/aws/credentials
mkdir -p ifs-auth-service/ifs-ldap-service/src/main/docker/certs/
mkdir -p ifs-auth-service/ifs-idp-service/src/main/docker/certs/
mkdir -p ifs-auth-service/ifs-sp-service/src/main/docker/certs/

echo -e "[$AWS_PROFILE]
aws_access_key_id = $KEY_ID
aws_secret_access_key = $KEY" > ifs-auth-service/aws/credentials

echo "Start copying secrets from parameter store"

docker image rm ssm-access-image
docker build --tag="ssm-access-image" docker/aws-cli
docker run -id --rm -e AWS_PROFILE=$AWS_PROFILE -v $PWD/ifs-auth-service/aws:/root/.aws --name ssm-access-container ssm-access-image

#1 = paramName
getParameter () {
  docker exec ssm-access-container aws ssm get-parameter --name $1 --with-decryption | jq ".Parameter.Value" | tr -d \"
}

#1 = paramValue, 2 = writePath
writeParameter () {
  echo -e $1 > $2
  echo $1
  echo $2
}

#SSM Parameter store can store up to 4k characters, therefore longer secrets are split when stored and reassembled here
#1 = paramValue 2 = writePath
appendParameter () {
  echo -e $1 >> $2
  echo $1
  echo $2
}

writeParameter "$(getParameter "/CI/IFS/$ENV/LDAP/ENCRYPTION/KEY")" "ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key"
writeParameter "$(getParameter "/CI/IFS/$ENV/LDAP/ENCRYPTION/CERT")" "ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt"
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/ENCRYPTION/KEY")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key"
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/ENCRYPTION/CERT")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt"
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/KEY")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem"
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/CERT")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem"
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/1")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/SIGNING/KEY")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key"
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/SIGNING/CERT")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt"
writeParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/KEY")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem"
writeParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/CERT")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem"
writeParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/1")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"

if [ "$ENV" == "NON-PROD" ]; then
  echo "env is non prod"
  appendParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/2")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"
  appendParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/3")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"

  appendParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/2")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"
  appendParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/3")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"

  writeParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/CERT")" "server.crt"
  writeParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/KEY")" "server.key"
  writeParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/CACERT/1")" "ca.crt"
  appendParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/CACERT/2")" "ca.crt"
  appendParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/CACERT/3")" "ca.crt"
fi

docker stop ssm-access-container

echo "Finished copying secrets from parameter store"
