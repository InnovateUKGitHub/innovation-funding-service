#!/bin/bash

eval $(docker-machine env default)

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

cd ../../../

docker-compose stop