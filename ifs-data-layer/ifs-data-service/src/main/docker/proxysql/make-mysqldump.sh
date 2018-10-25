#!/usr/bin/env bash

# take the data dump
mysqldump -u$DB_USER -p$DB_PASS -h127.0.0.1 -P6033 $DB_NAME --single-transaction --ignore-table=$DB_NAME._tempIFS --ignore-table=$DB_NAME._tempIFS_AssScore --ignore-table=$DB_NAME._tempIFS_CapUse > /dump/anonymised-dump.sql

DB_ANON_PASS=$DB_ANON_PASS

echo "Kieran" + $DB_ANON_PASS

# encrypt the dump file before transit and finally remove the unprotected file
gpg --yes --batch --passphrase=$DB_ANON_PASS -c /dump/anonymised-dump.sql
rm /dump/anonymised-dump.sql