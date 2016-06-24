#!/bin/bash

eval $(docker-machine env default)

BASEDIR=$(dirname "$0")
cd $BASEDIR

cd ../../../

docker-compose stop