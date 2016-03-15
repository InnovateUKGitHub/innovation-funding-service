#!/usr/bin/env bash

filename=$(bash _get-latest-shibboleth-image-filename.sh)
shibboleth_location=$(bash _get-shibboleth-install-location.sh)

cd ${shibboleth_location}

if [ -z "$filename" ]; then

  echo "Unable to find Shibboleth Docker image file."
  exit 1

fi


docker load < $filename
