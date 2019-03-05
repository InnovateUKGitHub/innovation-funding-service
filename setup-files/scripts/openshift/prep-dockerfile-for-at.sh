#!/bin/bash

sed -i.bak "s#VOLUME\ \/mnt/ifs_storage#VOLUME\ \/tmp#g" ifs-data-layer/docker/Dockerfile-template
sed -i.bak "/USER\ gluster/d" ifs-data-layer/docker/Dockerfile-template
sed -i.bak "/umask\ 0002/d"  setup-files/scripts/docker/set-umask0002-and-run-jar.sh

