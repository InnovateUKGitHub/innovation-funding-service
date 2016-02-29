#!/usr/bin/env bash
docker-machine stop default
##VBoxManage modifyvm default --natpf1 delete https
##VBoxManage modifyvm default --natpf1 "https,tcp,127.0.0.1,443,,443"
docker-machine start default
docker-machine env
eval $(docker-machine env)
docker stop ifs-local-dev
docker rm ifs-local-dev
docker run -d --add-host=host_ip:10.0.2.2 -p 443:443 --name ifs-local-dev g2g3/ifs-local-dev

##???
cp /etc/hosts /tmp/hostsbackup
ip_address=$(docker-machine ip)
cat /etc/hosts | grep -v 'ifs-local-dev' > /tmp/temphosts
echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
sudo cp /tmp/temphosts /etc/hosts

##
docker exec ifs-local-dev sed -i s/172\.17\.0\.1/host_ip/ /etc/apache2/sites-enabled/shibvhost.conf
docker exec ifs-local-dev service apache2 reload




