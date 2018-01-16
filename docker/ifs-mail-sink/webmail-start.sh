#!/bin/bash

./generate-certs.sh

. /etc/apache2/envvars
/etc/init.d/courier-authdaemon start
/etc/init.d/sqwebmail start
rm -fr /var/run/apache2/*
apache2 -DFOREGROUND
