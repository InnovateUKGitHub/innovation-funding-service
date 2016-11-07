#!/bin/bash
echo "**This script requires / does the following:"
echo "**A local database with username: root and password: password"
echo "**An export from the live database called ifs_current_live.sql"
echo "Creating the database if it does not exist"
mysql -uroot -ppassword -e "CREATE SCHEMA IF NOT EXISTS ifs_current_live"
echo "**Importing the live database into the ifs_current_live"
mysql -uroot -ppassword ifs_current_live < ../baseline/ifs_current_live.sql
mysql -uroot -ppassword -e "SELECT script \
  FROM ifs_baseline.schema_version AS blsv \
 WHERE blsv.version NOT IN (SELECT version FROM ifs_current_live.schema_version);" > generated/scripts_not_run_on_live_to_check.txt
