#!/usr/bin/env bash

./stop-shibboleth.sh
./start-shibboleth.sh
docker_id=$(./_get-shibboleth-instance-id.sh)

sleep 10

docker exec $docker_id rm /etc/apache2/sites-available/shibvhost.conf
docker cp ../../../ifs-auth-service/shibboleth-apache-config.conf $docker_id:/etc/apache2/sites-available/shibvhost.conf
docker exec $docker_id service apache2 reload
