#!/bin/bash

cd ../../run/mac
source _mac-set-docker-vars.sh

cd -
cd ../common
bash _upgrade-shibboleth.sh
