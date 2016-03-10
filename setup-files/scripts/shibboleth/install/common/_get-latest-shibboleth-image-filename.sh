#!/usr/bin/env bash

shibboleth_install_location=$(bash _get-shibboleth-install-location.sh)

cd ${shibboleth_install_location}

ls | grep 'g2g3-ifs-local-dev' | sort | tail -1
