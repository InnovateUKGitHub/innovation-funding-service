#!/bin/bash

REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com

LDAP_VERSION=0.4.0
IDP_VERSION=0.5.5
SP_VERSION=0.5.2

docker login -p $(oc whoami -t) -e unused -u unused ${REGISTRY}

if [ -z $(docker images -q innovateuk/ifs-shib-sp:${SP_VERSION}) ]
then
    docker pull docker-registry-default.apps.prod.ifs-test-clusters.com/innovateuk/ifs-shib-sp:${SP_VERSION}
fi

if [ -z $(docker images -q innovateuk/ifs-shib-idp:${IDP_VERSION}) ]
then
    docker pull docker-registry-default.apps.prod.ifs-test-clusters.com/innovateuk/ifs-shib-idp:${IDP_VERSION}
fi

if [ -z $(docker images -q innovateuk/ifs-ldap:${LDAP_VERSION}) ]
then
    docker pull docker-registry-default.apps.prod.ifs-test-clusters.com/innovateuk/ifs-ldap:${LDAP_VERSION}
fi

docker tag ${REGISTRY}/innovateuk/ifs-shib-idp:${IDP_VERSION} innovateuk/ifs-shib-idp:${IDP_VERSION}
docker tag ${REGISTRY}/innovateuk/ifs-ldap:${LDAP_VERSION} innovateuk/ifs-ldap:${LDAP_VERSION}
docker tag ${REGISTRY}/innovateuk/ifs-shib-sp:${SP_VERSION} innovateuk/ifs-shib-sp:${SP_VERSION}


echo "SUCCESS"