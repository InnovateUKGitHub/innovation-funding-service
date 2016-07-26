#!/bin/bash

BASEDIR=$(dirname "$0")
cd $BASEDIR

eval $(docker-machine env default)

./_delete-shib-users-remote.sh
./_install-or-upgrade.sh
./syncShib.sh