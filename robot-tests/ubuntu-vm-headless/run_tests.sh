#!/usr/bin/env bash
rm -rf /tmp/ifs/
pybot --outputdir /robot-tests/target --pythonpath /robot-tests/IFS_acceptance_tests/libs -v SERVER_BASE:ifs-local-dev -v POSTCODE_LOOKUP_IMPLEMENTED:'NO' -v UPLOAD_FOLDER:/robot-tests/upload_files -v DOWNLOAD_FOLDER:/vagrant/download_files -v VIRTUAL_DISPLAY:true -v PROTOCOL:https:// --exclude Pending --exclude Failing --exclude FailingForLocal --exclude Email $@ --name IFS /robot-tests/IFS_acceptance_tests
