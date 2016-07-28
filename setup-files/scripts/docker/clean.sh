#!/bin/bash

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

eval $(docker-machine env default)
cd ../../../

docker-compose -p ifs down --rmi all -v --remove-orphans