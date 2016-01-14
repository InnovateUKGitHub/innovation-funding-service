#!/bin/bash


function readLine {
    read
    echo $REPLY
}

function toLowerCase {
    $(echo $1 | tr '[:upper]' '[:lower]')
}









echo ""
echo ""
echo "This script generates an LDIF record for a User, based on a consistent template.  This can then be used to populate larger .ldif files that can be used to populate LDAP directories with a user set."
echo ""
echo ""

echo "First name e.g. Steve: "
firstName=$(readLine)
firstNameLower=$(toLowerCase $firstName)

echo "Last name e.g. Smith: "
lastName=$(readLine)
lastNameLower=$(toLowerCase $lastName)

uid="$firstNameLower.$lastNameLower"
fullName="$firstName $lastName"

echo "dn: cn=$fullName,uid=$uid,dc=nodomain"
echo "objectClass: top"
echo "objectClass: person"
echo "objectClass: organizationalPerson"
echo "objectClass: inetOrgPerson"
echo "uid: $uid"
echo "cn: $fullName"
echo "displayName: $fullName"
echo "givenName: $firstName"
echo "sn: $lastName"
#mail: steve.smith@empire.com
#title: Mr
#userPassword: test
