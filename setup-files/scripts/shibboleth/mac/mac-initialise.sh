#!/usr/bin/env bash
docker-machine start default
source _mac-set-docker-vars.sh
bash _mac-edit-hosts.sh
bash _mac-purge-and-start.sh









