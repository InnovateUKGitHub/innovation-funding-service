#!/bin/sh

. shibboleth-setup-functions.sh

LOCAL_CONFIG_DIR=/etc/shibboleth/extras

mkdir /etc/shibboleth/metadata/

# Template IDP / SP entity properties
createEntitiesFromProperties

echo << EOF

Summary
-------
SP/IDP entity descriptor metadata
EOF

ls /etc/shibboleth/metadata/*.xml