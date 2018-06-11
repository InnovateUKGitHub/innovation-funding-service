package org.innovateuk.ifs.survey;

import org.innovateuk.ifs.commons.service.RootAnonymousUserRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.RootDefaultRestTemplateAdaptor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class IntegrationTestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        return new AsyncRestTemplate();
    }

    @Bean
    public RootDefaultRestTemplateAdaptor tootDefaultRestTemplateAdaptor() {
        return new RootDefaultRestTemplateAdaptor();
    }

    @Bean
    public RootAnonymousUserRestTemplateAdaptor rootAnonymousUserRestTemplateAdaptor() {
        return new RootAnonymousUserRestTemplateAdaptor();
    }
}
