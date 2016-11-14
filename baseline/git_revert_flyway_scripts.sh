#!/bin/bash
echo "**REVERTING ALL CHANGES TO THE FLYWAY SCRIPTS IN THE CODEBASE. THIS WILL NEED TO BE REMOVED FOR THE FINAL COMMIT"
rm -rf ../ifs-data-service/src/main/resources/db
git checkout ../ifs-data-service/src/main/resources/db/*
rm -rf ../ifs-data-service/build
