#!/bin/bash
# R Popple: Depending on environment variables clean migrate the database and sync it with ldap.
if ! [ $ONLY_SYNC_LDAP = "true" ] ; then
. /clean-migrate-db.sh
fi

if [ ${PERFORMANCE_APPLICANT_USERS} > 0 ] || [ ${PERFORMANCE_h2020_USERS} > 0 ] ; then
. /add-performance-users.sh
fi

. /wipe-ldap-users.sh

. /ldap-sync-from-ifs-db.sh