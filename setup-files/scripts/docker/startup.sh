#!/bin/bash

setEnv() {
    case $OSTYPE in
        darwin*)
            echo "Mac detected"
            eval $(docker-machine env default)
            ;;
        linux*)
            echo "Linux detected"
            ;;
        *)
            echo "Unable to determine a supported operating system for this script.  Currently only supported on Linux and Macs"
            exit 1
            ;;
    esac
}

setHostFile(){
    case $OSTYPE in
        darwin*)
            cp /etc/hosts /tmp/hostsbackup
            ip_address=$(docker-machine ip default)
            cat /etc/hosts | grep -v 'ifs-local-dev' | grep -v 'iuk-auth-localdev' > /tmp/temphosts
            echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
            echo "$ip_address  iuk-auth-localdev" >> /tmp/temphosts
            echo "$ip_address  ifs-database" >> /tmp/temphosts
            sudo cp /tmp/temphosts /etc/hosts
            ;;
        linux*)
            cp /etc/hosts /tmp/hostsbackup
            ip_address=`docker inspect --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ifs-local-dev`
            database_ip_address=`docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ifs-database`
            cat /etc/hosts | grep -v 'ifs-local-dev' | grep -v 'iuk-auth-localdev' > /tmp/temphosts
            echo "$ip_address  iuk-auth-localdev" >> /tmp/temphosts
            echo "$ip_address  ifs-local-dev" >> /tmp/temphosts
            echo "$database_ip_address  ifs-database" >> /tmp/temphosts
            sudo cp /tmp/temphosts /etc/hosts
            ;;
        *)
            echo "Unable to determine a supported operating system for this script.  Currently only supported on Linux and Macs"
            exit 1
            ;;
    esac
}

installShib() {
    cd setup-files/scripts/shibboleth
    ./install-or-upgrade.sh
    cd ../../../
}

cd ../../../
setEnv
installShib
docker-compose up -d
sleep 2
docker-compose exec mysql mysql -uroot -ppassword -e 'create database ifs_test'
docker-compose exec mysql mysql -uroot -ppassword -e 'create database ifs'
setHostFile
./gradlew -Pprofile=docker flywayClean flywayMigrate
cd setup-files/scripts/docker
./syncShib.sh
