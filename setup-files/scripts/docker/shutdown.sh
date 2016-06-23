#!/bin/bash

eval $(docker-machine env default)

cd ../../../

docker-compose stop