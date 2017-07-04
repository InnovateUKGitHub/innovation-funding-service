#!/bin/sh

echo $DB_USER
echo $DB_PASS

sed -i "s#<<DB_USER>>#${DB_USER}#g" /etc/proxysql.cnf
sed -i "s#<<DB_PASS>>#${DB_PASS}#g" /etc/proxysql.cnf
sed -i "s#<<DB_NAME>>#${DB_NAME}#g" /etc/proxysql.cnf
sed -i "s#<<DB_HOST>>#${DB_HOST}#g" /etc/proxysql.cnf
sed -i "s#<<DB_PORT>>#${DB_PORT}#g" /etc/proxysql.cnf

proxysql -f