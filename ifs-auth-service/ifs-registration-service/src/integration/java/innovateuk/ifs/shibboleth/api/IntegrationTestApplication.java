package org.innovateuk.ifs.shibboleth.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.ldap.test.EmbeddedLdapServerFactoryBean;
import org.springframework.ldap.test.LdifPopulator;

public class IntegrationTestApplication extends Application {

    @Autowired
    private LdapProperties ldapProperties;

    @Value("classpath:/integration-tests.ldif")
    private Resource integrationTestsLdif;


    @Bean(name = "embeddedLdapServer")
    public EmbeddedLdapServerFactoryBean embeddedLdapServer() throws Exception {

        final EmbeddedLdapServerFactoryBean factoryBean = new EmbeddedLdapServerFactoryBean();
        factoryBean.setPartitionName("integration-test");
        factoryBean.setPort(ldapProperties.getPort());
        factoryBean.setPartitionSuffix(ldapProperties.getBaseDn());

        return factoryBean;
    }


    @Bean
    public LdifPopulator ldifPopulator() {
        final LdifPopulator ldifPopulator = new LdifPopulator();

        ldifPopulator.setContextSource(ldapContextSource());
        ldifPopulator.setResource(integrationTestsLdif);
        ldifPopulator.setBase(ldapProperties.getBaseDn());
        ldifPopulator.setDefaultBase(ldapProperties.getBaseDn());
        ldifPopulator.setClean(true);

        return ldifPopulator;
    }
}
