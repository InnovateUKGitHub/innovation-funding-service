#!/usr/bin/env bash
set -e

# take the data dump
mysqldump -u$DB_USER -p$DB_PASS -h127.0.0.1 -P6033 $DB_NAME --single-transaction --debug-info --extended-insert --max_allowed_packet=1073741824 --ignore-table=$DB_NAME._ApplicationSetUp --ignore-table=$DB_NAME._ApplicationSetUp_CapUse --ignore-table=$DB_NAME._ApplicationSetUp_Value --ignore-table=$DB_NAME._tempIFS --ignore-table=$DB_NAME._tempIFS_AssScore --ignore-table=$DB_NAME._tempIFS_CapUse > /dump/anonymised-dump.sql

# encrypt the dump file before transit and finally remove the unprotected file
gpg --yes --batch --passphrase=$DB_ANON_PASS -c /dump/anonymised-dump.sql
rm /dump/anonymised-dump.sql