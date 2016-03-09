#!/usr/bin/env bash
bash deploy-ui.sh
docker exec ifs-local-dev rm /tmp/_build-and-deploy-remote.sh
docker cp _build-and-deploy-remote.sh ifs-local-dev:/tmp/_build-and-deploy-remote.sh
docker exec ifs-local-dev /tmp/_build-and-deploy-remote.sh


