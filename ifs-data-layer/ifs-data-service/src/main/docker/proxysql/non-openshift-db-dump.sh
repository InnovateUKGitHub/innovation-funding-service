#!/bin/bash

#
# This script is for testing anonymised dumps against a local docker-compose environment as opposed to a local or remote
# OpenShift project
#
set -e

cd ../../../../../..

./gradlew :ifs-data-layer:ifs-data-service:dbAnonymisedDumpDocker -x test

docker run --name anonymised-data-service --net ifs -d \
  -e DB_NAME='ifs' \
  -e DB_USER='root' \
  -e DB_PASS='password' \
  -e DB_HOST='ifs-database' \
  -e DB_PORT='3306' \
  innovateuk/db-anonymised-data

sleep 3

docker exec -it anonymised-data-service /dump/make-mysqldump.sh

sleep 1

docker cp anonymised-data-service:/dump/anonymised-dump.sql.gpg /tmp/anonymised.sql.gpg

#docker kill anonymised-data-service && docker rm anonymised-data-service

echo "Now simply run 'gpg --decrypt /tmp/anonymised.sql.gpg > /tmp/anonymised.sql' specifying the password 'password' when prompted"