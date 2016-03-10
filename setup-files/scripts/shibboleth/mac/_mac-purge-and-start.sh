#!/usr/bin/env bash

source _mac-set-docker-vars.sh

cd ../common
bash _purge-shibboleth.sh

docker run -d --add-host=ifs-application-host:10.0.2.2 -p 443:443 --name ifs-local-dev g2g3/ifs-local-dev

