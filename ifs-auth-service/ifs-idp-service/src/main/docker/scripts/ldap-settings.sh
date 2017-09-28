#!/bin/bash

# A Munro 23 Feb 2017. Request from Nigel that all idp ldap settings are set via Docker env variables.
# UPDATE HISTORY:
# 9 Mar 2017 A Munro: added regapi.properties
# 4 Apr 2017: Updated for idp 3.3.1
# 28 Sep 2017: separated RegApi into own container

CONF=/opt/shibboleth-idp/conf/ldap.properties

cp $CONF $CONF.default

# A better way than using sed:
cat << EOF > $CONF
idp.authn.LDAP.ldapURL                          = $LDAPURL
idp.authn.LDAP.useStartTLS                      = $LDAPUSESTARTTLS
idp.authn.LDAP.useSSL                           = $LDAPUSESSL
idp.authn.LDAP.baseDN                           = $LDAPBASEDN
idp.authn.LDAP.bindDN                           = $LDAPBINDDN
idp.authn.LDAP.bindDNCredential                 = $LDAPBINDDNCRED
idp.authn.LDAP.userFilter                       = $LDAPUSERFILTER
idp.authn.LDAP.authenticator                    = $LDAPAUTHENTICATOR
idp.authn.LDAP.returnAttributes                 = mail,uid

idp.attribute.resolver.LDAP.ldapURL             = $LDAPRURL
idp.attribute.resolver.LDAP.baseDN              = $LDAPRBASEDN
idp.attribute.resolver.LDAP.bindDN              = $LDAPRBINDDN
idp.attribute.resolver.LDAP.bindDNCredential    = $LDAPRBINDDNCRED
idp.attribute.resolver.LDAP.useStartTLS         = $LDAPRUSESTARTTLS
idp.attribute.resolver.LDAP.trustCertificates   = $LDAPRTRUSTCERT
idp.attribute.resolver.LDAP.searchFilter        = $LDAPRSEARCHFILTER
idp.attribute.resolver.LDAP.returnAttributes    = $LDAPRRETURNATTRIBUTE
idp.pool.LDAP.validatePeriod                    = $LDAPVALIDATEPERIOD
EOF

[ $IDPVER = "3.3.1" ] && {
  cat << EOF >> $CONF
idp.authn.LDAP.trustCertificates                = %{idp.home}/credentials/ldap-encryption.crt
idp.authn.LDAP.trustStore                       = %{idp.home}/credentials/ldap-server.truststore
idp.authn.LDAP.returnAttributes                 = mail,uid
idp.authn.LDAP.dnFormat                         = uid=%s,%{idp.authn.LDAP.baseDN}

idp.attribute.resolver.LDAP.connectTimeout      = %{idp.authn.LDAP.connectTimeout:PT3S}
idp.attribute.resolver.LDAP.responseTimeout     = %{idp.authn.LDAP.responseTimeout:PT3S}
EOF
}