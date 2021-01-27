#!/bin/bash

. ldap-base-commands.sh

emails=$1
for email in $(echo $emails | sed "s/,/ /g")
do
    addUserToShibboleth "$email"
done