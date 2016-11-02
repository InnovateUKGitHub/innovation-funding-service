#!/bin/bash

function coloredEcho() {
    local exp=$1;
    local color=$2;
    if ! [[ ${color} =~ '^[0-9]$' ]] ; then
       case $(echo ${color} | tr '[:upper:]' '[:lower:]') in
        black) color=0 ;;
        red) color=1 ;;
        green) color=2 ;;
        yellow) color=3 ;;
        blue) color=4 ;;
        magenta) color=5 ;;
        cyan) color=6 ;;
        white|*) color=7 ;; # white or invalid color
       esac
    fi
    tput setaf ${color};
    echo ${exp};
    tput sgr0;
}

# Absolutely moves to directory where this script is located
BASEDIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd ${BASEDIR}

echo
coloredEcho "=> Performing initial setup steps..." green
echo

if [ "$(uname -s)" == "Darwin" ] || [ "$(uname -s)" == "Linux" ]
then
    coloredEcho "Adding addresses to your /etc/hosts..." blue
    ./hosts-helper.sh
else
    coloredEcho "Could not automatically add addresses to /etc/hosts. Skipping..." yellow
fi

# Setup the default .env file if it does not already exist
if [ ! -f ./.env ]; then
    coloredEcho "Creating .env file..." blue
    cp ./.env-defaults ./.env
fi

# Check there is a Shibboleth image that can be used
echo
coloredEcho "Making sure Shibboleth image is loaded into Docker..." green
echo
./scripts/_install-or-upgrade.sh

wait

# Start up the Docker containers
docker-compose -p ifs up -d --force-recreate

wait
sleep 5

docker-compose -p ifs exec mysql bash -c 'mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "CREATE DATABASE IF NOT EXISTS ifs_test"'
docker-compose -p ifs exec mysql bash -c 'mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "CREATE DATABASE IF NOT EXISTS ifs"'

wait
sleep 3

cd ../../../
./gradlew -Pprofile=docker flywayClean flywayMigrate
cd ${BASEDIR}
./scripts/_delete-shib-users-remote.sh
./syncShib.sh

# Make sure that the Docker environment have been setup properly
echo
coloredEcho "=> Starting cleanDeploy for all projects..." green
echo
./deploy.sh all "$@"