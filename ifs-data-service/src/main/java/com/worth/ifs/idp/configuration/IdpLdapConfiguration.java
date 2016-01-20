package com.worth.ifs.idp.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 *
 */
@Configuration
public class IdpLdapConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public LdapContextSource contextSource () {
        LdapContextSource contextSource= new LdapContextSource();
        contextSource.setUrl(env.getRequiredProperty("idp.stub.ldap.url"));
        contextSource.setBase(env.getRequiredProperty("idp.stub.ldap.base"));
        contextSource.setUserDn(env.getRequiredProperty("idp.stub.ldap.user"));
        contextSource.setPassword(env.getRequiredProperty("idp.stub.ldap.password"));
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
}
