#!/usr/bin/env bash

cd ../common
bash _purge-shibboleth.sh

docker run --cap-add=SYS_PTRACE -d --add-host=ifs-application-host:172.17.0.1 --name ifs-local-dev g2g3/ifs-local-dev
