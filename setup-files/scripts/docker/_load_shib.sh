#!/bin/bash

eval $(docker-machine env default)

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

cd shibImages

filename=$(ls | grep 'g2g3-ifs-local-dev' | sort | tail -1)


if [ -z "$filename" ]; then

  echo "Unable to find Shibboleth Docker image file."
  exit 1

fi

docker load < ${filename}