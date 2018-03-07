#!/bin/bash
# R Popple: Depending on environment variables clean migrate the database and sync it with ldap.
if [ $DB_RESET_TASK = "ONLY_SYNC_LDAP" ] ; then
    echo "Synchronising LDAP"
    ./ldap-sync-from-ifs-db.sh
elif [ $DB_RESET_TASK = "CLEAN_DB_AND_SYNC_LDAP" ] ; then
    echo "Resetting DB data and synchronising LDAP"
    ./clean-migrate-db.sh
    ./ldap-sync-from-ifs-db.sh
elif [ $DB_RESET_TASK = "BASELINE_ONLY" ] ; then
    echo "Baselining DB"
    ./baseline-db.sh
else
    echo "No database changes specified.  Set DB_RESET_TASK to one of ONLY_SYNC_LDAP, CLEAN_DB_AND_SYNC_LDAP or BASELINE_ONLY to perform appropriate task."
fi