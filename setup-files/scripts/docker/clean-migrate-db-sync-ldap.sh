#!/bin/bash
# R Popple: Depending on environment variables clean migrate the database and sync it with ldap.
if [ $DB_RESET_TASK = "ONLY_SYNC_LDAP" ] ; then
    ./ldap-sync-from-ifs-db.sh
elif [ $DB_RESET_TASK = "CLEAN_DB_AND_SYNC_LDAP" ] ; then
    ./clean-migrate-db.sh
    ./ldap-sync-from-ifs-db.sh
elif [ $DB_RESET_TASK = "BASELINE_ONLY" ] ; then
    ./baseline-db.sh
fi