#!/usr/bin/env bash
cp /etc/hosts /tmp/hostsbackup
ip_address=`docker inspect --format '{{ .NetworkSettings.IPAddress }}' ifs-local-dev`
cat /etc/hosts | grep -v 'ifs-local-dev' > /tmp/temphosts
echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
sudo cp /tmp/temphosts /etc/hosts
