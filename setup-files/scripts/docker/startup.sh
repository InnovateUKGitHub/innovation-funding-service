#!/bin/bash

sudo echo "Entering sudo now so the script doesn't hang later!"

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

eval $(docker-machine env default)

# clean up old untagged images
docker rmi $(docker images | grep "^<none>" | awk '{print $3}') > /dev/null 2>&1

#TODO check if shibboleth image exists, if not install it.
cd ../../../
docker-compose -p ifs up -d
wait
sleep 1
docker-compose -p ifs exec mysql mysql -uroot -ppassword -e 'create database if not exists ifs_test'
docker-compose -p ifs exec mysql mysql -uroot -ppassword -e 'create database if not exists ifs'

cd $BASEDIR
./set-hosts.sh
./migrate.sh
