#!/usr/bin/env bash
docker cp _delete-shib-users-remote.sh ifs-local-dev:/tmp/_delete-shib-users-remote.sh
docker exec ifs-local-dev /tmp/delete-shib-users-remote.sh

