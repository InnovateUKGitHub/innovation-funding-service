#!/bin/bash

docker-compose -p ifs stop shib
docker-compose -p ifs rm ifs-local-dev

./_load_shib.sh
