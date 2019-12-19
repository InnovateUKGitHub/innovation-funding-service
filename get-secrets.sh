#!/bin/bash
FILE=docker/aws-cli/Dockerfile

if test -f "$FILE"; then

    echo "Start copying secrets from parameter store"

    docker image rm ssm-access-image
    docker build --tag="ssm-access-image" docker/aws-cli

    mkdir -p ifs-auth-service/ifs-ldap-service/src/main/docker/certs/
    echo "/CI/IFS/LDAP/ENCRYPTION/KEY"
    IFS_LDAP_ENCRYPTION_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/LDAP/ENCRYPTION/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_LDAP_ENCRYPTION_KEY > ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key
    echo "$(cat ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key)"
    echo "/CI/IFS/LDAP/ENCRYPTION/CERT"
    IFS_LDAP_ENCRYPTION_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/LDAP/ENCRYPTION/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_LDAP_ENCRYPTION_CERT > ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt
    echo "$(cat ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt)"

    mkdir -p ifs-auth-service/ifs-idp-service/src/main/docker/certs/
    echo "/CI/IFS/IDP/ENCRYPTION/KEY"
    IFS_IDP_ENCRYPTION_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/ENCRYPTION/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_ENCRYPTION_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key)"
    echo "/CI/IFS/IDP/ENCRYPTION/CERT"
    IFS_IDP_ENCRYPTION_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/ENCRYPTION/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_ENCRYPTION_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt)"

    echo "/CI/IFS/IDP/PROXY/KEY"
    IFS_IDP_PROXY_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/PROXY/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_PROXY_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem)"
    echo "/CI/IFS/IDP/PROXY/CERT"
    IFS_IDP_PROXY_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/PROXY/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_PROXY_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem)"
    echo "/CI/IFS/IDP/PROXY/CACERT"
    IFS_IDP_PROXY_CACERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameters-by-path --path "/CI/IFS/IDP/PROXY/CACERT" --with-decryption | jq -r ".Parameters|sort_by(.Name)[].Value")
    echo $IFS_IDP_PROXY_CACERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem)"

    echo "/CI/IFS/IDP/SIGNING/KEY"
    IFS_IDP_SIGNING_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/SIGNING/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_SIGNING_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key)"
    echo "/CI/IFS/IDP/SIGNING/CERT"
    IFS_IDP_SIGNING_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/SIGNING/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_SIGNING_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt)"

    mkdir -p ifs-auth-service/ifs-sp-service/src/main/docker/certs/
    echo "/CI/IFS/SP/PROXY/KEY"
    IFS_SP_PROXY_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/SP/PROXY/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_SP_PROXY_KEY > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem
    echo "$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem)"
    echo "/CI/IFS/SP/PROXY/CERT"
    IFS_SP_PROXY_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/SP/PROXY/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_SP_PROXY_CERT > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem
    echo "$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem)"
    echo "/CI/IFS/SP/PROXY/CACERT"
    IFS_SP_PROXY_CACERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameters-by-path --path "/CI/IFS/SP/PROXY/CACERT" --with-decryption | jq -r ".Parameters|sort_by(.Name)[].Value")
    echo $IFS_SP_PROXY_CACERT > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem
    echo "$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem)"

    echo "/CI/IFS/SIGNED/CERT"
    IFS_SIGNED_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/SIGNED/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_SIGNED_CERT > server.crt
    echo "$(cat server.crt)"
    echo "/CI/IFS/SIGNED/KEY"
    IFS_SIGNED_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/SIGNED/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_SIGNED_KEY > server.key
    echo "$(cat server.key)"
    echo "/CI/IFS/SIGNED/CACERT"
    IFS_SIGNED_CACERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameters-by-path --path "/CI/IFS/SIGNED/CACERT" --with-decryption | jq -r ".Parameters|sort_by(.Name)[].Value")
    echo $IFS_SIGNED_CACERT > ca.crt
    echo "$(cat ca.crt)"
END
    echo "Finished copying secrets from parameter store"

else
    echo "Could not copy secrets from parameter store"
fi
