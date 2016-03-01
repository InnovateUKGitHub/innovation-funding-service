#!/usr/bin/env bash
.mac-set-env.sh
docker stop ifs-local-dev
docker rm ifs-local-dev
docker run -d --add-host=actual_host_ip:10.0.2.2 -p 443:443 --name ifs-local-dev g2g3/ifs-local-dev

