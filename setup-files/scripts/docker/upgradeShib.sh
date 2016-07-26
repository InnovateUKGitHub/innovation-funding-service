#!/bin/bash

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

eval $(docker-machine env default)

./_delete-shib-users-remote.sh
./_install-or-upgrade.sh
./syncShib.sh