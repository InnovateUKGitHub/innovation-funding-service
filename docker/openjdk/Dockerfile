FROM adoptopenjdk/openjdk8:x86_64-ubuntu-jdk8u242-b08-slim

# Needed for docker-compose health checks
RUN apt-get install -yq curl

# Take all latest patches
RUN apt-get update && apt-get upgrade -y && apt-get autoremove -y
