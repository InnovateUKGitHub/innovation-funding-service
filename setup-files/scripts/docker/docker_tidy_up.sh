#!/usr/bin/env bash

VERSION=$1

# Remove docker images
docker rmi $(docker images | grep application-service | grep -v $VERSION)
docker rmi $(docker images | grep assessment-service | grep -v $VERSION)
docker rmi $(docker images | grep competition-management-service | grep -v $VERSION)
docker rmi $(docker images | grep project-setup-service | grep -v $VERSION)
docker rmi $(docker images | grep project-setup-management-service | grep -v $VERSION)

docker rmi $(docker images | grep innovateuk/data-service | grep -v $VERSION)


# Note if you are using docker toolbox this is not going to work.  Please consider upgrading.
docker system prune -f