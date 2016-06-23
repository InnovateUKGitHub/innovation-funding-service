#!/bin/bash

setHostFile(){
    cp /etc/hosts /tmp/hostsbackup
    ip_address=$(docker-machine ip default)
    cat /etc/hosts | grep -v 'ifs-local-dev' | grep -v 'iuk-auth-localdev' > /tmp/temphosts
    echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
    echo "$ip_address  iuk-auth-localdev" >> /tmp/temphosts
    echo "$ip_address  ifs-database" >> /tmp/temphosts
    sudo cp /tmp/temphosts /etc/hosts

}

eval $(docker-machine env default)
./_delete-shib-users-remote.sh
./_install-or-upgrade.sh
cd ../../../
docker-compose up -d
wait
docker-compose exec mysql mysql -uroot -ppassword -e 'create database ifs_test'
docker-compose exec mysql mysql -uroot -ppassword -e 'create database ifs'
setHostFile
./gradlew -Pprofile=docker flywayClean flywayMigrate
cd setup-files/scripts/docker
./syncShib.sh
