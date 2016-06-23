#!/bin/bash

eval $(docker-machine env default)
cd ../../../

docker-compose down --rmi all -v --remove-orphans