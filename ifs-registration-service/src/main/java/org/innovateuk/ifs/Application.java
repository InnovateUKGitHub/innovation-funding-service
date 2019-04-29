package org.innovateuk.ifs;

import org.innovateuk.ifs.shibboleth.api.LdapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;


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

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
