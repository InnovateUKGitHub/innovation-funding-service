#!/bin/bash

# A Munro 23 Feb 2017. Request from Nigel that all idp ldap settings are set via Docker env variables.
# UPDATE HISTORY:
# 9 Mar 2017 A Munro: added regapi.properties
# 4 Apr 2017: Updated for idp 3.3.1

CONF=/opt/shibboleth-idp/conf/ldap.properties
APICONF=/etc/tomcat8/regapi.properties

# Default env settings are set in Dockerfile
# Defaults:
#REGAPIKEY="1234567890"
#LDAPURL="ldap://ldap:389"
#LDAPPORT="389"
#LDAPUSESTARTTLS=false
#LDAPBASEDN="dc=nodomain"
#LDAPBINDDN="cn=admin,dc=nodomain"
#LDAPBINDDNCRED="default"
#LDAPPPOLICYDN="cn=PPolicy,ou=Policies"
#LDAPPPOLICY="true"
##LDAPUSERFILTER="(\&(mail={user})(!(employeeType=inactive)))"
#LDAPUSERFILTER="(&(mail={user})(!(employeeType=inactive)))"
#LDAPAUTHENTICATOR="anonSearchAuthenticator"
#LDAPRURL="%{idp.authn.LDAP.ldapURL}"
#LDAPRBASEDN="%{idp.authn.LDAP.baseDN:undefined}"
#LDAPRBINDDN="%{idp.authn.LDAP.bindDN:undefined}"
#LDAPRBINDDNCRED="%{idp.authn.LDAP.bindDNCredential:undefined}"
#LDAPRUSESTARTTLS="%{idp.authn.LDAP.useStartTLS:true}"
#LDAPRTRUSTCERT="%{idp.authn.LDAP.trustCertificates:undefined}"
#LDAPRSEARCHFILTER="(mail=\$resolutionContext.principal)"
#LDAPRRETURNATTRIBUTE="mail,uid"
#LDAPVALIDATEPERIOD="PT5M" 3.2.1: 20; 3.3.1: PT20S; note the changed format from secs to xml!!!

# ldap.properties default settings:
#idp.authn.LDAP.ldapURL                          = ldap://ldap:389
#idp.authn.LDAP.useStartTLS                      = false
#idp.authn.LDAP.baseDN                           = dc=nodomain
#idp.authn.LDAP.bindDN                           = cn=admin,dc=nodomain
#idp.authn.LDAP.bindDNCredential                 = default
#idp.authn.LDAP.userFilter                       = (&(mail={user})(!(employeeType=inactive)))
#idp.authn.LDAP.authenticator                    = anonSearchAuthenticator
#idp.authn.LDAP.dnFormat                         = uid=%s,%{idp.authn.LDAP.baseDN}
#idp.authn.LDAP.trustStore                       = %{idp.home}/credentials/ldap-server.truststore
#idp.authn.LDAP.trustCertificates                = %{idp.home}/credentials/ldap-server.crt
#idp.authn.LDAP.returnAttributes                 = mail,uid

#idp.attribute.resolver.LDAP.ldapURL             = %{idp.authn.LDAP.ldapURL}
#idp.attribute.resolver.LDAP.baseDN              = %{idp.authn.LDAP.baseDN:undefined}
#idp.attribute.resolver.LDAP.bindDN              = %{idp.authn.LDAP.bindDN:undefined}
#idp.attribute.resolver.LDAP.bindDNCredential    = %{idp.authn.LDAP.bindDNCredential:undefined}
#idp.attribute.resolver.LDAP.useStartTLS         = %{idp.authn.LDAP.useStartTLS:true}
#idp.attribute.resolver.LDAP.trustCertificates   = %{idp.authn.LDAP.trustCertificates:undefined}
#idp.attribute.resolver.LDAP.searchFilter        = (mail=$resolutionContext.principal)
#idp.attribute.resolver.LDAP.returnAttributes    = mail,uid
#idp.attribute.resolver.LDAP.connectTimeout      = %{idp.authn.LDAP.connectTimeout:PT3S}
#idp.attribute.resolver.LDAP.responseTimeout     = %{idp.authn.LDAP.responseTimeout:PT3S}
#idp.pool.LDAP.validatePeriod                    = see LDAPVALIDATEPERIOD above

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

cp $APICONF $APICONF.default

cat << EOF2 > $APICONF
##
## Authentication Keys
##
shibboleth.api.keys[0]=$REGAPIKEY

##
## LDAP Configuration
##
shibboleth.ldap.port=$LDAPPORT
shibboleth.ldap.url=$LDAPURL
shibboleth.ldap.user=$LDAPBINDDN
shibboleth.ldap.password=$LDAPBINDDNCRED
shibboleth.ldap.baseDn=$LDAPBASEDN

##
## Password Policy
##
shibboleth.ldap.ppolicyAttrib=$LDAPPPOLICYDN
shibboleth.ldap.requireValidPPolicy=$LDAPPPOLICY

shibboleth.password.policy.blacklist[0]=welcome123
shibboleth.password.policy.blacklist[1]=welcome1234
shibboleth.password.policy.blacklist[2]=welcome1
shibboleth.password.policy.blacklist[3]=password123
shibboleth.password.policy.blacklist[4]=password1234
shibboleth.password.policy.blacklist[5]=testtest1
shibboleth.password.policy.blacklist[6]=test1test1
shibboleth.password.policy.blacklist[7]=1testtest
shibboleth.password.policy.blacklist[8]=123test123
shibboleth.password.policy.blacklist[9]=test1234
shibboleth.password.policy.blacklist[10]=admin123
shibboleth.password.policy.blacklist[11]=123admin
shibboleth.password.policy.blacklist[12]=qwertyui1
shibboleth.password.policy.blacklist[13]=changeme1
shibboleth.password.policy.blacklist[14]=xxxxxxx1
shibboleth.password.policy.blacklist[15]=abc123abc
shibboleth.password.policy.blacklist[16]=letmein123
shibboleth.password.policy.blacklist[17]=inn0vate
EOF2
