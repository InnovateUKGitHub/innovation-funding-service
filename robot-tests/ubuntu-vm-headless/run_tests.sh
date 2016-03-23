#!/usr/bin/env bash

pybot --outputdir /robot-tests/target --pythonpath /robot-tests/IFS_acceptance_tests/libs -v SERVER_BASE:ifs-local-dev  -v PROTOCOL:https:// --exclude Pending --exclude Failing --exclude FailingForLocal --name IFS /robot-tests/IFS_acceptance_tests