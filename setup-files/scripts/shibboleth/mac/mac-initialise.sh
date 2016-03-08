#!/usr/bin/env bash
docker-machine start default
eval $(docker-machine env)
sh _mac-edit-hosts.sh
sh _mac-purge-and-start.sh









