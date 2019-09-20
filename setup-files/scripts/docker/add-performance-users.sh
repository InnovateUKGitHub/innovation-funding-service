#!/bin/bash

# Inserts users into db
# Args:
# $1: db host (default ifs-database)
# $2: db (database name; default ifs)
# $3: db user (default root)
# $4: db user password (default 'password;)
# $5: db port (default 3306)

host=$DB_HOST
db=$DB_NAME
user=$DB_USER
pass=$DB_PASS
port=$DB_PORT

[ ! -z "$1" ] && host=$1
[ ! -z "$2" ] && db=$2
[ ! -z "$3" ] && user=$3
[ ! -z "$4" ] && pass=$4
[ ! -z "$5" ] && port=$5

echo host:$host
echo database:$db
echo user:$user
echo port:$port

executeMySQLCommand() {
    mysql $db -P $port -u $user --password=$pass -h $host -N -s -e "$1"
}

# look at passing in parameter
application_id=$(executeMySQLCommand "select id from application where name = 'Base Perf App';")
for (( u=2; u<=${PERFORMANCE_APPLICANT_USERS}; u++ ))
do
    email="perf.applicant${u}@example.com"
    executeMySQLCommand "INSERT INTO user (email, first_name, last_name, status, uid, system_user, allow_marketing_emails, created_by, created_on, modified_by, modified_on) VALUES ('$email', 'firstName', 'lastName', 'ACTIVE', UUID(), '0', '0', '16', now(), '16', now());
    SET @inserted_user_id = last_insert_id();
    INSERT INTO user_role (role_id, user_id) values (4, @inserted_user_id);
    INSERT INTO process_role (application_id, organisation_id, role_id, user_id) VALUES ($application_id, '21', '1', @inserted_user_id);
    INSERT INTO user_terms_and_conditions (user_id, terms_and_conditions_id, accepted_date) VALUES (@inserted_user_id, '9', now());"
done