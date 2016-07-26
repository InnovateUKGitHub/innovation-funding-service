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

BASEDIR=$(dirname "$0")
cd $BASEDIR

eval $(docker-machine env default)
#TODO check if shibboleth image exists, if not install it.
cd ../../../
docker-compose up -d
wait
docker-compose exec mysql mysql -uroot -ppassword -e 'create database ifs_test'
docker-compose exec mysql mysql -uroot -ppassword -e 'create database ifs'
setHostFile

cd $BASEDIR
./migrate.sh
