#!/bin/bash


serverBase='localhost:8085'



if [ -n "$1" ]; then
  serverBase="$1"
fi





java -jar selenium-server-standalone-2.48.2.jar &
pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$serverBase -v DESIRED_CAPABILITIES:"browserName:htmlunit,javascriptEnabled:false" IFS_acceptance_tests/tests/*
