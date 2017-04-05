#!/bin/bash
set -e

LDAP_VERSION=0.4.0
IDP_VERSION=0.5.2
SP_VERSION=0.5.2

REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR
if [ ! -d shibImages ]; then
    mkdir shibImages
fi
cd shibImages

eval `ssh-agent -s`

if [ -z $(docker images -q innovateuk/ifs-shib-sp:${SP_VERSION}) ]
then
    SP_FILE=innovateuk-ifs-shib-sp-${SP_VERSION}.tar.gz
    if [ ! -f ${SP_FILE} ]; then
        echo "Downloading innovateuk/ifs-shib-sp:${SP_VERSION}"
        sftp iuk-ifs@incoming.g2g3digital.net:/incoming/${SP_FILE}
    fi
    docker load < ${SP_FILE}
else
    echo "Image innovateuk/ifs-shib-sp:${SP_VERSION} already EXISTS."
fi

if [ -z $(docker images -q innovateuk/ifs-shib-idp:${IDP_VERSION}) ]
then
    IDP_FILE=innovateuk-ifs-shib-idp-${IDP_VERSION}.tar.gz
    if [ ! -f ${IDP_FILE} ]; then
        echo "Downloading innovateuk/ifs-shib-idp:${IDP_VERSION}"
        sftp iuk-ifs@incoming.g2g3digital.net:/incoming/${IDP_FILE}
    fi
    docker load < ${IDP_FILE}
else
    echo "Image innovateuk/ifs-shib-idp:${IDP_VERSION} already EXISTS."
fi

if [ -z $(docker images -q innovateuk/ifs-ldap:${LDAP_VERSION}) ]
then
    LDAP_FILE=innovateuk-ifs-ldap-${LDAP_VERSION}.tar.gz
    if [ ! -f ${LDAP_FILE} ]; then
        echo "Downloading innovateuk/ifs-ldap:${LDAP_VERSION}"
        sftp iuk-ifs@incoming.g2g3digital.net:/incoming/${LDAP_FILE}
    fi
    docker load < ${LDAP_FILE}
else
    echo "Image innovateuk/ifs-ldap:${LDAP_VERSION} already EXISTS."
fi

docker tag innovateuk/ifs-shib-idp:${IDP_VERSION} ${REGISTRY}/innovateuk/ifs-shib-idp:${IDP_VERSION}
docker tag innovateuk/ifs-ldap:${LDAP_VERSION} ${REGISTRY}/innovateuk/ifs-ldap:${LDAP_VERSION}
docker tag innovateuk/ifs-shib-sp:${SP_VERSION} ${REGISTRY}/innovateuk/ifs-shib-sp:${SP_VERSION}

docker push ${REGISTRY}/innovateuk/ifs-shib-idp:${IDP_VERSION}
docker push ${REGISTRY}/innovateuk/ifs-ldap:${LDAP_VERSION}
docker push ${REGISTRY}/innovateuk/ifs-shib-sp:${SP_VERSION}


echo "SUCCESS"
