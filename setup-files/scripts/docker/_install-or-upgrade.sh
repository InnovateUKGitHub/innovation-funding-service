#!/bin/bash

BASEDIR=$(dirname "$0")
cd $BASEDIR

docker stop ifs-local-dev
docker rm ifs-local-dev

cd shibImages

filename=$(ls | grep 'g2g3-ifs-local-dev' | sort | tail -1)


if [ -z "$filename" ]; then

  echo "Unable to find Shibboleth Docker image file."
  exit 1

fi


docker load < ${filename}