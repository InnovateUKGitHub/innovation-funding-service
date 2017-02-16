#!/usr/bin/env bash

# first sftp images from https://devops.innovateuk.org/documentation/pages/viewpage.action?pageId=8455893
# and docker load them

REGISTRY=docker-registry-default.apps.prod.ifs-test-clusters.com

docker tag innovateuk/ifs-shib-idp:0.2.0 ${REGISTRY}/innovateuk/ifs-shib-idp:0.2.0
docker tag innovateuk/ifs-ldap:0.2.1 ${REGISTRY}/innovateuk/ifs-ldap:0.2.1
docker tag innovateuk/ifs-shib-idp:0.2.0 ${REGISTRY}/innovateuk/ifs-shib-sp:0.2.0

docker push ${REGISTRY}/innovateuk/ifs-shib-idp:0.2.0
docker push ${REGISTRY}/innovateuk/ifs-ldap:0.2.1
docker push ${REGISTRY}/innovateuk/ifs-shib-sp:0.2.0
