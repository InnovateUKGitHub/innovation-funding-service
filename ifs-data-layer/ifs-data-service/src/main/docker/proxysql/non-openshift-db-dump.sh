#!/bin/bash

#
# This script is for testing anonymised dumps against a local docker-compose environment as opposed to a local or remote
# OpenShift project
#

set -e

docker build -t innovateuk/proxysql -f Dockerfile-proxysql .

docker run --name innovationfundingservice_proxysql_1 --net innovationfundingservice_ifs -d innovateuk/proxysql

sleep 3

docker exec -it innovationfundingservice_proxysql_1 /etc/make-mysqldump.sh

sleep 1

docker cp innovationfundingservice_proxysql_1:/dump/anonymised-dump.sql.gpg /tmp

docker kill innovationfundingservice_proxysql_1 && docker rm innovationfundingservice_proxysql_1