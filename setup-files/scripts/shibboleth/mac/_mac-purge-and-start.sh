#!/usr/bin/env bash
eval $(docker-machine env)
docker stop ifs-local-dev
docker rm ifs-local-dev
docker run -d --add-host=actual_host_ip:10.0.2.2 -p 443:443 --name ifs-local-dev g2g3/ifs-local-dev

