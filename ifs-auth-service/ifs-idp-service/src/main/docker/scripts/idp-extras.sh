#!/bin/sh

. shibboleth-setup-functions.sh

LOCAL_CONFIG_DIR=/etc/shibboleth/extras
SHIBBOLETH_CONF_DIR=/etc/shibboleth

# Configuration
replacePlaceholders /opt/shibboleth-idp/conf/logback.xml IDP_LOG_LEVEL

mkdir /etc/shibboleth/metadata/

# Template IDP / SP entity properties
createEntitiesFromProperties

echo << EOF

Summary
-------
SP/IDP entity descriptor metadata
EOF

ls /etc/shibboleth/metadata/*.xml