package org.innovateuk.ifs;

import org.innovateuk.ifs.shibboleth.api.LdapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.repository.config.EnableLdapRepositories;


@SpringBootApplication
@EnableConfigurationProperties
@EnableLdapRepositories
public class Application extends SpringBootServletInitializer {

    @Autowired
    private LdapProperties ldapProperties;


    @Bean
    public LdapContextSource ldapContextSource() {
        final LdapContextSource context = new LdapContextSource();

        context.setUrl(ldapProperties.getUrl());
        context.setUserDn(ldapProperties.getUser());
        context.setPassword(ldapProperties.getPassword());
        context.setBase(ldapProperties.getBaseDn());
        context.setPooled(ldapProperties.getNativePooling());

        return context;
    }


    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(ldapContextSource());
    }

}
