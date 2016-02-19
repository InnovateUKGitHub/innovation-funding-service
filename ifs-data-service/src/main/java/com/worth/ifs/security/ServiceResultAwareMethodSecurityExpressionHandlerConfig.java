package com.worth.ifs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

/**
 *
 */
@Configuration
public class ServiceResultAwareMethodSecurityExpressionHandlerConfig {

    @Bean
    public DefaultMethodSecurityExpressionHandler getCustomMethodSecurityExpressionHandler() {
        return new ServiceResultAwareMethodSecurityExpressionHandler();
    }
}
