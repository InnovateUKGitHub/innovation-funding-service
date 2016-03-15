#!/usr/bin/env bash

pybot --outputdir /robot-tests/target --pythonpath /robot-tests/IFS_acceptance_tests/libs -v SERVER_BASE:10.0.2.2:8080 -v PROTOCOL:http:// --exclude Pending --exclude Failing --exclude FailingForLocal --name IFS /robot-tests/IFS_acceptance_tests