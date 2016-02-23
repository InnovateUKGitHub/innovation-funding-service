#!/usr/bin/env bash

cd $(./_get-shibboleth-install-location.sh)

ls | grep 'g2g3-ifs-local-dev' | sort | tail -1
