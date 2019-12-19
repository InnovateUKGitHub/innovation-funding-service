#!/bin/bash
FILE=docker/aws-cli/Dockerfile

if test -f "$FILE"; then

    echo "Start copying secrets from parameter store"

    #mkdir aws
    #touch aws/credentials

    docker image rm ssm-access-image
    docker build --tag="ssm-access-image" docker/aws-cli

    mkdir -p ifs-auth-service/ifs-ldap-service/src/main/docker/certs/
    echo "just created directory"
    IFS_LDAP_ENCRYPTION_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v $PWD/aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/LDAP/ENCRYPTION/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo "just made call to aws"
    echo $IFS_LDAP_ENCRYPTION_KEY > ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key
    echo "just wrote to file"
    echo "$(cat ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.key)"
    : <<'END'

    IFS_LDAP_ENCRYPTION_CERT=$(env AWS_PROFILE=iukorg docker run --rm -it -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/LDAP/ENCRYPTION/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_LDAP_ENCRYPTION_CERT > ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt
    echo "$(cat ifs-auth-service/ifs-ldap-service/src/main/docker/certs/ldap-encryption.crt)"

    mkdir -p ifs-auth-service/ifs-idp-service/src/main/docker/certs/
    IFS_IDP_ENCRYPTION_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/ENCRYPTION/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_ENCRYPTION_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.key)"
    IFS_IDP_ENCRYPTION_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/ENCRYPTION/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_ENCRYPTION_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-encryption.crt)"

    IFS_IDP_PROXY_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/PROXY/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_PROXY_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_key.pem)"
    IFS_IDP_PROXY_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/PROXY/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_PROXY_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_certificate.pem)"
    IFS_IDP_PROXY_CACERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameters-by-path --path "/CI/IFS/IDP/PROXY/CACERT" --with-decryption | jq -r ".Parameters|sort_by(.Name)[].Name")
    echo $IFS_IDP_PROXY_CACERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp_proxy_cacertificate.pem)"

    IFS_IDP_SIGNING_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/SIGNING/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_SIGNING_KEY > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.key)"
    IFS_IDP_SIGNING_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/IDP/SIGNING/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_IDP_SIGNING_CERT > ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt
    echo "$(cat ifs-auth-service/ifs-idp-service/src/main/docker/certs/idp-signing.crt)"

    mkdir -p ifs-auth-service/ifs-sp-service/src/main/docker/certs/
    IFS_SP_PROXY_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/SP/PROXY/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_SP_PROXY_KEY > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem
    echo "$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_key.pem)"
    IFS_SP_PROXY_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/SP/PROXY/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_SP_PROXY_CERT > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem
    echo "$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_certificate.pem)"
    IFS_SP_PROXY_CACERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameters-by-path --path "/CI/IFS/SP/PROXY/CACERT" --with-decryption | jq -r ".Parameters|sort_by(.Name)[].Name")
    echo $IFS_SP_PROXY_CACERT > ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem
    echo "$(cat ifs-auth-service/ifs-sp-service/src/main/docker/certs/sp_proxy_cacertificate.pem)"

    IFS_SIGNED_CERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/SIGNED/CERT" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_SIGNED_CERT > server.crt
    echo "$(cat server.crt)"
    IFS_SIGNED_KEY=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameter --name "/CI/IFS/SIGNED/KEY" --with-decryption | jq -r ".Parameter.Value")
    echo $IFS_SIGNED_KEY > server.key
    echo "$(cat server.key)"
    IFS_SIGNED_CACERT=$(env AWS_PROFILE=iukorg docker run --rm -e AWS_PROFILE -v ~/.aws:/root/.aws ssm-access-image aws ssm get-parameters-by-path --path "/CI/IFS/SIGNED/CACERT" --with-decryption | jq -r ".Parameters|sort_by(.Name)[].Name")
    echo $IFS_SIGNED_CACERT > ca.crt
    echo "$(cat ca.crt)"
END
    echo "Finished copying secrets from parameter store"

else
    echo "Could not copy secrets from parameter store"
fi
