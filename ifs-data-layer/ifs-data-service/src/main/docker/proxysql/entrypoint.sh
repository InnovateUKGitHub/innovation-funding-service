#!/bin/sh

sed -i "s#<<DB_USER>>#$DB_USER#g;s#<<DB_PASS>>#$DB_PASS#g;s#<<DB_NAME>>#$DB_NAME#g;s#<<DB_HOST>>#$DB_HOST#g;s#<<DB_PORT>>#$DB_PORT#g" /etc/proxysql.cnf

proxysql -f