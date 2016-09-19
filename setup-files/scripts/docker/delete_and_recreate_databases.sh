#!/bin/bash


# run commands on docker instead of the host machine
eval $(docker-machine env default)


# delete and recreate the databases
docker-compose -p ifs exec mysql mysql -uroot -ppassword -e 'drop database ifs;'
docker-compose -p ifs exec mysql mysql -uroot -ppassword -e 'drop database ifs_test;'
docker-compose -p ifs exec mysql mysql -uroot -ppassword -e 'create database ifs'
docker-compose -p ifs exec mysql mysql -uroot -ppassword -e 'create database ifs_test'


# go to the right directory
BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR
cd ../../..


# migrate data into the newly created databases
./gradlew -Pprofile=docker flywayClean flywayMigrate
