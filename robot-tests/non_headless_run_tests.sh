#!/usr/bin/env bash
rm -rf /tmp/ifs/
pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v BROWSER:GoogleChrome -v SERVER_BASE:ifs-local-dev -v POSTCODE_LOOKUP_IMPLEMENTED:'NO' -v UPLOAD_FOLDER:/Users/rav/hive/iuk/innovation-funding-service/robot-tests/upload_files -v DOWNLOAD_FOLDER:/Users/rav/hive/iuk/innovation-funding-service/robot-tests/ubuntu-vm-headless/download_files -v VIRTUAL_DISPLAY:false -v PROTOCOL:https:// --exclude Failing --exclude Pending --exclude FailingForLocal --exclude PendingForLocal --exclude Email --name IFS IFS_acceptance_tests/tests
