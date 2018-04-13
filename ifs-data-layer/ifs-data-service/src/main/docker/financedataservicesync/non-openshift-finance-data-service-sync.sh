#!/bin/bash

#
# This script is for testing syncing the finance cost totals against a local docker-compose environment
# as opposed to a local or remote OpenShift project
#

set -e

dbhost=ifs-database
db=ifs
dbuser=root
dbpass=password
dbport=3306

financedbhost=ifs-finance-database
financedb=ifs_finance
financedbuser=root
financedbpass=password
financedbport=3306

datahost=data-service
dataport=8080

docker build -t innovateuk/finance-data-service-sync -f Dockerfile-financedataservicesync .

docker run --entrypoint ./send-all-cost-totals.sh --name innovationfundingservice_finance-data-service-sync_1 --net innovationfundingservice_ifs -d innovateuk/finance-data-service-sync $dbhost $db $dbuser $dbpass $dbport $financedbhost $financedb $financedbuser $financedbpass $financedbport $datahost $dataport

sleep 3

docker logs innovationfundingservice_finance-data-service-sync_1
docker rm innovationfundingservice_finance-data-service-sync_1