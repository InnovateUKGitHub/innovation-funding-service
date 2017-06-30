#!/bin/sh

cp /etc/proxysql.cnf /tmp/proxysql.cnf

echo $DB_USER
echo $DB_PASS

sed -i "s#<<DB_USER>>#${DB_USER}#g" /tmp/proxysql.cnf
sed -i "s#<<DB_PASS>>#${DB_PASS}#g" /tmp/proxysql.cnf
sed -i "s#<<DB_NAME>>#${DB_NAME}#g" /tmp/proxysql.cnf
sed -i "s#<<DB_HOST>>#${DB_HOST}#g" /tmp/proxysql.cnf
sed -i "s#<<DB_PORT>>#${DB_PORT}#g" /tmp/proxysql.cnf

cat /tmp/proxysql.cnf

mv /tmp/proxysql.cnf /etc/proxysql.cnf

cat /etc/proxysql.cnf

proxysql -f