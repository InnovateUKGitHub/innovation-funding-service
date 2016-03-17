#!/usr/bin/env bash

cd ../../run/common

bash _purge-shibboleth.sh

cd -

bash _install-latest-shibboleth-image.sh
