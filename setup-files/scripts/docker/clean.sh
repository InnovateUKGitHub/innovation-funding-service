#!/bin/bash

BASEDIR=$(dirname "$0")
cd $BASEDIR

eval $(docker-machine env default)
cd ../../../

docker-compose down --rmi all -v --remove-orphans