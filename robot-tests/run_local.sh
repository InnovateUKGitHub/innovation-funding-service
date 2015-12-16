#!/bin/bash

serverBase='localhost:8085'
if [ -n "$1" ]; then
 serverBase="$1"
fi

pip install simplejson requests

pybot --outputdir target --pythonpath IFS_acceptance_tests/libs -v SERVER_BASE:$serverBase IFS_acceptance_tests/tests/*
