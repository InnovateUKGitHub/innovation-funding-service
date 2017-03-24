#!/usr/bin/env bash
docker exec innovationfundingservice_idp_1 rm -rf  /opt/shibboleth-idp/views
docker exec innovationfundingservice_idp_1 rm -rf  /opt/shibboleth-idp/messages
docker cp ../../../../ifs-auth-service/views innovationfundingservice_idp_1:/opt/shibboleth-idp/views
docker cp ../../../../ifs-auth-service/messages innovationfundingservice_idp_1:/opt/shibboleth-idp/messages
