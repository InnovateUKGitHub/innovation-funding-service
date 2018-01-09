#!/bin/bash
# R Popple: Depending on environment variables clean migrate the database and sync it with ldap.
if ! [ $ONLY_SYNC_LDAP = "true" ] ; then
. /clean-migrate-db.sh
fi
. /ldap-sync-from-ifs-db.sh