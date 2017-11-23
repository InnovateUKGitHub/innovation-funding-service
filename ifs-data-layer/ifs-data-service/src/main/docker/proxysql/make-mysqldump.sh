#!/usr/bin/env bash

# take the data dump
mysqldump -u$DB_USER -p$DB_PASS -h127.0.0.1 -P6033 $DB_NAME --skip-extended-insert > /dump/anonymised-dump.sql
mysqldump -u$DB_USER -p$DB_PASS -h127.0.0.1 -P6033 $DB_NAME > /dump/anonymised-dump-extended-insert.sql

# encrypt the dump file before transit and finally remove the unprotected file
gpg --yes --batch --passphrase=$DB_PASS -c /dump/anonymised-dump.sql
rm /dump/anonymised-dump.sql