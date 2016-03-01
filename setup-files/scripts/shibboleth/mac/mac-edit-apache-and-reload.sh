#!/usr/bin/env bash
.mac-set-env.sh
docker exec ifs-local-dev sed -i s/172\.17\.0\.1/actual_host_ip/ /etc/apache2/sites-enabled/shibvhost.conf
docker exec ifs-local-dev service apache2 reload
