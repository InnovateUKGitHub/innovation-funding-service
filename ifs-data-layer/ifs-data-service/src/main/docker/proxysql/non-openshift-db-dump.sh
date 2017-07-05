#!/usr/bin/env bash

set -e

docker build -t innovateuk/proxysql -f Dockerfile-proxysql .

docker run --name innovationfundingservice_proxysql_1 --net innovationfundingservice_ifs -d innovateuk/proxysql

sleep 3

docker exec -it innovationfundingservice_proxysql_1 /etc/make-mysqldump.sh

sleep 1

docker cp innovationfundingservice_proxysql_1:/tmp/dump.sql /tmp

docker kill innovationfundingservice_proxysql_1 && docker rm innovationfundingservice_proxysql_1