#!/bin/bash

docker login --username ${1} --password ${2} docker-ifs.devops.innovateuk.org

docker run --name ${3} --cidfile "cid" -d docker-ifs.devops.innovateuk.org/${3}:latest /bin/sh -c "while true; do sleep 1; done" 

dockerId=`cat cid`

ready=$(docker inspect -f {{.State.Running}} ${dockerId})

if [[ ${ready} ]];
then
  docker cp ${dockerId}:/app/${3} ${3} 
fi

while [[ ! -f ${3} ]];
do
  sleep 1
done

docker kill ${dockerId}
docker rm ${dockerId}
docker logout
rm cid

