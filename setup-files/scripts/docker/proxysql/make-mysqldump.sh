#!/usr/bin/env bash

mysqldump -u$DB_USER -p$DB_PASS -h127.0.0.1 -P6033 $DB_NAME --skip-extended-insert | gzip -c > /dump/anonymised-dump.sql.gz