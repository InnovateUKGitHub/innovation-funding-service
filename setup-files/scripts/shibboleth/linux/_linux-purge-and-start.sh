#!/usr/bin/env bash
docker stop ifs-local-dev
docker rm ifs-local-dev
docker run -d --add-host=ifs-application-host:172.17.0.1 --name ifs-local-dev g2g3/ifs-local-dev

