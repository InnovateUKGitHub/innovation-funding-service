#!/bin/bash

. ldap-base-commands.sh

wipeLdapUsers

for u in findAllUsersInDatabase
do
  addUserToShibboleth "$u"
done