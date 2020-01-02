#!/bin/bash
ENV=$1
echo "Start copying secrets from parameter store"

docker image rm ssm-access-image
docker build --tag="ssm-access-image" docker/aws-cli

#1 = paramName
getParameter () {
  docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "$1" --with-decryption | jq ".Parameter.Value" | tr -d \"
}

#1 = paramValue 2 = writePath
writeParameter () {
  echo -e $1 > $2
}

#1 = paramValue 2 = writePath
appendParameter () {
  echo -e $1 >> $2
}

mkdir -p ifs-auth-service/ifs-ldap-service/src/main/docker/certs
#paramValue=$(getParameter "/CI/IFS/$ENV/LDAP/ENCRYPTION/KEY")
#echo $paramValue
writeParameter "$(getParameter "/CI/IFS/$ENV/LDAP/ENCRYPTION/KEY")" "ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key"
#IFS_LDAP_ENCRYPTION_KEY=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/LDAP/ENCRYPTION/KEY" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_LDAP_ENCRYPTION_KEY > ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key
writeParameter "$(getParameter "/CI/IFS/$ENV/LDAP/ENCRYPTION/CERT")" "ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key"
#IFS_LDAP_ENCRYPTION_CERT=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/LDAP/ENCRYPTION/CERT" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_LDAP_ENCRYPTION_CERT > ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt

mkdir -p ifs-auth-service/ifs-idp-service/src/main/docker/certs/
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/ENCRYPTION/KEY")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key"
#IFS_IDP_ENCRYPTION_KEY=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/ENCRYPTION/KEY" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_IDP_ENCRYPTION_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/ENCRYPTION/CERT")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt"
#IFS_IDP_ENCRYPTION_CERT=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/ENCRYPTION/CERT" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_IDP_ENCRYPTION_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt

writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/KEY")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem"
#IFS_IDP_PROXY_KEY=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/PROXY/KEY" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_IDP_PROXY_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/CERT")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem"
#IFS_IDP_PROXY_CERT=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/PROXY/CERT" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_IDP_PROXY_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem

writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/1")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"
appendParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/2")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"
appendParameter "$(getParameter "/CI/IFS/$ENV/IDP/PROXY/CACERT/3")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem"
#IFS_IDP_PROXY_CACERT_1=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/PROXY/CACERT/1" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_IDP_PROXY_CACERT_2=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/PROXY/CACERT/2" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_IDP_PROXY_CACERT_3=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/PROXY/CACERT/3" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_IDP_PROXY_CACERT="$IFS_IDP_PROXY_CERT_1\n$IFS_IDP_PROXY_CERT_2\n$IFS_IDP_PROXY_CERT_3"
#echo -e $IFS_IDP_PROXY_CACERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem

writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/SIGNING/KEY")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key"
#IFS_IDP_SIGNING_KEY=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/SIGNING/KEY" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_IDP_SIGNING_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key
writeParameter "$(getParameter "/CI/IFS/$ENV/IDP/SIGNING/CERT")" "ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt"
#IFS_IDP_SIGNING_CERT=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/IDP/SIGNING/CERT" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_IDP_SIGNING_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt

mkdir -p ifs-auth-service/ifs-sp-service/src/main/docker/certs/
writeParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/KEY")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem"
#IFS_SP_PROXY_KEY=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SP/PROXY/KEY" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_SP_PROXY_KEY > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem

writeParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/CERT")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem"
#IFS_SP_PROXY_CERT=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SP/PROXY/CERT" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_SP_PROXY_CERT > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem

writeParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/1")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"
appendParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/2")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"
appendParameter "$(getParameter "/CI/IFS/$ENV/SP/PROXY/CACERT/3")" "ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem"
#IFS_SP_PROXY_CACERT_1=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SP/PROXY/CACERT/1" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_SP_PROXY_CACERT_2=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SP/PROXY/CACERT/2" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_SP_PROXY_CACERT_3=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SP/PROXY/CACERT/3" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_SP_PROXY_CACERT="$IFS_SP_PROXY_CACERT_1\n$IFS_SP_PROXY_CACERT_2\n$IFS_SP_PROXY_CACERT_3"
#echo -e $IFS_SP_PROXY_CACERT > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem

writeParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/CERT")" "server.crt"
#IFS_SIGNED_CERT=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SIGNED/CERT" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_SIGNED_CERT > server.crt

writeParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/KEY")" "server.key"
#IFS_SIGNED_KEY=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SIGNED/KEY" --with-decryption | jq ".Parameter.Value" | tr -d \")
#echo -e $IFS_SIGNED_KEY > server.key

writeParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/CACERT/1")" "ca.crt"
appendParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/CACERT/2")" "ca.crt"
appendParameter "$(getParameter "/CI/IFS/$ENV/SIGNED/CACERT/3")" "ca.crt"
#IFS_SIGNED_CACERT_1=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SIGNED/CACERT/1" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_SIGNED_CACERT_2=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SIGNED/CACERT/2" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_SIGNED_CACERT_3=$(docker run --rm -e AWS_PROFILE=iukorg -v $PWD/ifs-auth-service/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/$ENV/SIGNED/CACERT/3" --with-decryption | jq ".Parameter.Value" | tr -d \")
#IFS_SIGNED_CACERT="$IFS_SIGNED_CACERT_1\n$IFS_SIGNED_CACERT_2\n$IFS_SIGNED_CACERT_3"
#echo -e $IFS_SIGNED_CACERT > ca.crt

echo "Finished copying secrets from parameter store"
