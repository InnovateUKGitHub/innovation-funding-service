docker load < project-setup-service.tar
docker load < project-setup-management-service.tar
docker load < competition-management-service.tar
docker load < assessment-service.tar
docker load < application-service.tar
docker load < data-service.tar

docker tag $(docker images -q worth/project-setup-service) 172.30.53.244:5000/laszlo7/project-setup-service:1.0-SNAPSHOT
docker tag $(docker images -q worth/project-setup-management-service) 172.30.53.244:5000/laszlo7/project-setup-management-service:1.0-SNAPSHOT
docker tag $(docker images -q worth/competition-management-service) 172.30.53.244:5000/laszlo7/competition-management-service:1.0-SNAPSHOT
docker tag $(docker images -q worth/assessment-service) 172.30.53.244:5000/laszlo7/assessment-service:1.0-SNAPSHOT
docker tag $(docker images -q worth/application-service) 172.30.53.244:5000/laszlo7/application-service:1.0-SNAPSHOT
docker tag $(docker images -q worth/data-service) 172.30.53.244:5000/laszlo7/data-service:1.0-SNAPSHOT

docker push 172.30.53.244:5000/laszlo7/project-setup-service:1.0-SNAPSHOT
docker push 172.30.53.244:5000/laszlo7/project-setup-management-service:1.0-SNAPSHOT
docker push 172.30.53.244:5000/laszlo7/competition-management-service:1.0-SNAPSHOT
docker push 172.30.53.244:5000/laszlo7/assessment-service:1.0-SNAPSHOT
docker push 172.30.53.244:5000/laszlo7/application-service:1.0-SNAPSHOT
docker push 172.30.53.244:5000/laszlo7/data-service:1.0-SNAPSHOT