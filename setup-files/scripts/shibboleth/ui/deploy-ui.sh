#!/usr/bin/env bash
docker exec ifs-local-dev rm -rf  /opt/shibboleth-idp/views
docker exec ifs-local-dev rm -rf  /opt/shibboleth-idp/webapp
docker exec ifs-local-dev rm -rf  /opt/shibboleth-idp/messages
docker cp ../../../../ifs-auth-service/views ifs-local-dev:/opt/shibboleth-idp/views
docker cp ../../../../ifs-auth-service/webapp ifs-local-dev:/opt/shibboleth-idp/webapp
docker cp ../../../../ifs-auth-service/messages ifs-local-dev:/opt/shibboleth-idp/messages

