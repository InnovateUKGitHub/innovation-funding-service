#!/bin/bash

BASEDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $BASEDIR

cd shibImages

  spfilename=$(ls -t | grep 'innovateuk-ifs-shib-sp-'    | tail -1)
 idpfilename=$(ls -t | grep 'innovateuk-ifs-shib-idp-'   | tail -1)
ldapfilename=$(ls -t | grep 'innovateuk-ifs-ldap-'       | tail -1)


if [ -z "$spfilename" ]; then

  echo "Unable to find Shibboleth service provider Docker image file."
  exit 1

fi

if [ -z "$idpfilename" ]; then

  echo "Unable to find Shibboleth identity provider Docker image file."
  exit 1

fi
if [ -z "$ldapfilename" ]; then

  echo "Unable to find Shibboleth ladp provider Docker image file."
  exit 1

fi


docker load < ${spfilename} &
docker load < ${idpfilename} &
docker load < ${ldapfilename} &