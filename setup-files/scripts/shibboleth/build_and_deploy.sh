#!/usr/bin/env bash

docker_id=$(./_get-shibboleth-instance-id.sh)

if [ -z "$docker_id" ]; then

  echo "No Shibboleth Docker image currently running."
  exit 1
fi

docker exec $docker_id rm -rf  /tmp/build_shibboleth.sh

