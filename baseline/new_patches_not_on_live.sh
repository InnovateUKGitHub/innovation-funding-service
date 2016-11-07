#!/bin/bash
mysql -uroot -ppassword -e "SELECT version \
  FROM ifs_baseline.schema_version AS blsv \
 WHERE blsv.version NOT IN (SELECT version FROM ifs_dry_run_live.schema_version);"
