#!/bin/bash
# Expectations: 
# The the clair-container-scan.sh is in the same directory as this script.

# cd into the directory of the script where the clair-container-scan.sh is expected to be found.
cd $(cd -P -- "$(dirname -- "$0")" && pwd -P)

# Check what to search for in the docker image names.
TAG_TO_SEARCH_FOR=$1
# Get all innovateuk docker container images which have the TAG_TO_SEARCH_FOR as part of their name.
containers=$(docker images | awk '{print $1":"$2}' | grep -E "$TAG_TO_SEARCH_FOR")

# Loop through all containers and scan them.
echo "------------------------------------------"
echo "--We will scan the following containers:--"
echo "------------------------------------------"
for container in $containers; do
  created_date=`docker inspect -f '{{ .Created }}' $container`
  printf "|%-150s| %s\n" $container "`date -d $created_date`"
done
echo "------------------------------------------" 
for container in $containers;
do
  echo "------------------------------------"
  echo "-- Scanning Container: $container --"
  echo "------------------------------------"
  ./clair-container-scan.sh -v -p $container
  echo "-------------------------------------------------------------------------------------------"
  echo "If there is no JSON output above this line, the container $container has no vulnerabilities"
  echo "-------------------------------------------------------------------------------------------"
done

echo "------------------------------------------"
echo "-----------SCAN COMPLETE------------------"
echo "------------------------------------------"
