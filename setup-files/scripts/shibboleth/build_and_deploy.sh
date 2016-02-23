#!/usr/bin/env bash

docker_id=$(./_get-shibboleth-instance-id.sh)

if [ -z "$docker_id" ]; then

  echo "No Shibboleth Docker image currently running."
  exit 1
fi
docker exec $docker_id rm /tmp/_build_and_deploy.sh
docker cp /home/richardpopple/Projects/ifs/Code/innovation-funding-service/setup-files/scripts/shibboleth/_build_and_deploy.sh $docker_id:/tmp/_build_and_deploy.sh
docker exec $docker_id /tmp/_build_and_deploy.sh


