#!/bin/bash

# container run script

# Remove any lingering pid files
for p in /var/run/apache2/apache2.pid /var/run/shibboleth/shibd.pid
do
  [ -e $p ] && rm -f $p
done

/usr/sbin/shibd -f -c /etc/shibboleth/shibboleth2.xml -p /var/run/shibboleth/shibd.pid -w 30
exec /usr/sbin/apache2ctl -D FOREGROUND
