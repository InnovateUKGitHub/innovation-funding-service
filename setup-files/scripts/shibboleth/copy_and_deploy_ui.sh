#!/usr/bin/env bash

docker_id=$(./_get-shibboleth-instance-id.sh)

if [ -z "$docker_id" ]; then

  echo "No Shibboleth Docker image currently running."
  exit 1
fi

docker exec $docker_id rm -rf  /opt/shibboleth-idp/views
docker exec $docker_id rm -rf  /opt/shibboleth-idp/webapp
docker exec $docker_id rm -rf  /opt/shibboleth-idp/messages
docker cp /home/richardpopple/Projects/ifs/Code/shibboleth-login-styling/views $docker_id:/opt/shibboleth-idp/views
docker cp /home/richardpopple/Projects/ifs/Code/shibboleth-login-styling/webapp $docker_id:/opt/shibboleth-idp/webapp
docker cp /home/richardpopple/Projects/ifs/Code/shibboleth-login-styling/messages $docker_id:/opt/shibboleth-idp/messages

