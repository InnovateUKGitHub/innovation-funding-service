#!/bin/bash

sudo echo "Entering sudo now so the script doesn't hang later!"
cp /etc/hosts /tmp/hostsbackup
ip_address=$(docker-machine ip default)
cat /etc/hosts | grep -v 'ifs-local-dev' | grep -v 'iuk-auth-localdev' | grep -v 'ifs-database' > /tmp/temphosts
echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
echo "$ip_address  iuk-auth-localdev" >> /tmp/temphosts
echo "$ip_address  ifs-database" >> /tmp/temphosts
sudo cp /tmp/temphosts /etc/hosts