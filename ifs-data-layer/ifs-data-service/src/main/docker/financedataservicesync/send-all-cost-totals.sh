#!/bin/sh

# Executes a data tier request to send cost totals for all submitted applications to the ifs-finance-data-service.
# Truncates db table ifs_finance.cost_total prior to executing the request.

# Args:
# $1: db host (default ifs-database)
# $2: db database name (default ifs)
# $3: db user (default root)
# $4: db user password (default 'password')
# $5: db port (default 3306)
# $6: finance db host (default ifs-finance-database)
# $7: finance db name (default ifs_finance)
# $8: finance db user (default root)
# $9: finance db user password (default 'password')
# $10: finance db port (default 3306)
# $11: ifs-data-service host (default data-service)
# $12: ifs-data-service port (default 8080)

dbhost=$DB_HOST
db=$DB_NAME
dbuser=$DB_USER
dbpass=$DB_PASS
dbport=$DB_PORT

financedbhost=$FINANCE_DB_HOST
financedb=$FINANCE_DB_NAME
financedbuser=$FINANCE_DB_USER
financedbpass=$FINANCE_DB_PASS
financedbport=$FINANCE_DB_PORT

datahost=$DATA_SERVICE_HOST
dataport=$DATA_SERVICE_PORT

[ ! -z "$1" ] && dbhost=$1
[ ! -z "$2" ] && db=$2
[ ! -z "$3" ] && dbuser=$3
[ ! -z "$4" ] && dbpass=$4
[ ! -z "$5" ] && dbport=$5

[ ! -z "$6" ] && financedbhost=$6
[ ! -z "$7" ] && financedb=$7
[ ! -z "$8" ] && financedbuser=$8
[ ! -z "$9" ] && financedbpass=$9
[ ! -z "${10}" ] && financedbport=${10}

[ ! -z "${11}" ] && datahost=${11}
[ ! -z "${12}" ] && dataport=${12}

echo dbhost:$dbhost
echo db:$db
echo dbuser:$dbuser
echo dbport:$dbport
echo financedbhost:$financedbhost
echo financedb:$financedb
echo financedbuser:$financedbuser
echo financedbport:$financedbport
echo datahost:$datahost
echo dataport:$dataport

executeMySQLCommand() {
    mysql $db -P $dbport -u $dbuser --password=$dbpass -h $dbhost --skip-column-names --silent -e "$1"
}

executeFinanceMySQLCommand() {
    mysql $financedb -P $financedbport -u $financedbuser --password=$financedbpass -h $financedbhost --skip-column-names --silent -e "$1"
}

executeDataServiceRequest() {
    curl -v -X PUT -H "IFS_AUTH_TOKEN: $1" "http://$datahost:$dataport$2"
}

# Main

uid=$(executeMySQLCommand "select uid from user where email='ifs_system_maintenance_user@innovateuk.org';")

executeFinanceMySQLCommand "truncate table cost_total;"

executeDataServiceRequest $uid "/cost/sendAll"