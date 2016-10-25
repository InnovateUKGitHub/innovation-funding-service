#!/bin/bash

# Absolutely moves to directory where this script is located
BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd ${BASEDIR}

# Setup the default .env file if it does not already exist
if [ ! -f ./.env ]; then
    echo "Creating .env file..."
    cp ./.env-defaults ./.env
fi

# Start up the Docker containers
docker-compose -p ifs up -d

# Make sure that the Docker environment have been setup properly
wait
sleep 3

docker-compose -p ifs exec mysql bash -c 'mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "CREATE DATABASE IF NOT EXISTS ifs_test"'
docker-compose -p ifs exec mysql bash -c 'mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "CREATE DATABASE IF NOT EXISTS ifs"'

wait
sleep 3

cd ../../../
./gradlew -Pprofile=docker flywayClean flywayMigrate
cd ${BASEDIR}
./scripts/_delete-shib-users-remote.sh
./syncShib.sh
./deploy.sh all