#!/usr/bin/env bash
source _mac-set-docker-vars.sh
cp /etc/hosts /tmp/hostsbackup
ip_address=$(docker-machine ip default)
cat /etc/hosts | grep -v 'ifs-local-dev' | grep -v 'iuk-auth-localdev' > /tmp/temphosts
echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
echo "$ip_address  iuk-auth-localdev" >> /tmp/temphosts
sudo cp /tmp/temphosts /etc/hosts
