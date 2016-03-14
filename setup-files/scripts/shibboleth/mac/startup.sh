#!/usr/bin/env bash
docker-machine start default
source _mac-set-docker-vars.sh
bash _mac-purge-and-start.sh
bash _mac-edit-hosts.sh
cd ../common
sleep 10
bash reset-users-from-database.sh









