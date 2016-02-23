#!/usr/bin/env bash

./_show-running-instances.sh | grep 'g2g3/ifs-local-dev' | awk '{print $1}'
