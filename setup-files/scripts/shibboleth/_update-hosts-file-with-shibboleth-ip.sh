cp /etc/hosts /tmp/hostsbackup


ip_address=`docker inspect --format '{{ .NetworkSettings.IPAddress }}' $(docker ps | tail -n 1 | awk '{print $1}')`

cat /etc/hosts | grep -v 'ifs-local-dev' > /tmp/temphosts
echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
sudo cp /tmp/temphosts /etc/hosts
