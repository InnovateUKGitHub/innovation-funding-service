#!/usr/bin/env bash

./stop-shibboleth.sh

./_install-latest-shibboleth-image.sh

./start-shibboleth.sh
