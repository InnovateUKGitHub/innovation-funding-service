#!/bin/bash

startingDir= pwd

case $OSTYPE in
    darwin*)
        echo "Mac detected"
        eval $(docker-machine env default)
        cp /etc/hosts /tmp/hostsbackup
        ip_address=$(docker-machine ip default)
        cat /etc/hosts | grep -v 'ifs-local-dev' | grep -v 'iuk-auth-localdev' > /tmp/temphosts
        echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
        echo "$ip_address  iuk-auth-localdev" >> /tmp/temphosts
        sudo cp /tmp/temphosts /etc/hosts
        ;;
    linux*)
        echo "Linux detected"
        cp /etc/hosts /tmp/hostsbackup
        ip_address=`docker inspect --format '{{ .NetworkSettings.IPAddress }}' ifs-local-dev`
        cat /etc/hosts | grep -v 'ifs-local-dev' | grep -v 'iuk-auth-localdev' > /tmp/temphosts
        echo "$ip_address  iuk-auth-localdev" >> /tmp/temphosts
        echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
        sudo cp /tmp/temphosts /etc/hosts
        ;;
    *)
        echo "Unable to determine a supported operating system for this script.  Currently only supported on Linux and Macs"
        exit 1
        ;;
esac

cd ../../../
docker-compose up -d
docker-compose exec data
docker-compose exec mysql mysql -uroot -ppassword -e 'create database ifs_test'
./gradlew flywayClean flywayMigrate