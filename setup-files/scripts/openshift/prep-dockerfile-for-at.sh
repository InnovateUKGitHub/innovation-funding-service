#!/bin/bash

cd ../../..

sed -i.bak "s#VOLUME\ \/mnt/ifs_storage#VOLUME\ \/tmp#g" ifs-data-layer/docker/Dockerfile-template
sed -i.bak "/USER\ gluster/d" ifs-data-layer/docker/Dockerfile-template

