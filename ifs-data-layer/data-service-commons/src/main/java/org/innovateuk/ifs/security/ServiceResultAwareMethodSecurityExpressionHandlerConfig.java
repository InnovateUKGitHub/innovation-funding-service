package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.evaluator.AbstractCustomPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

/**
 *
 */
@Configuration
public class ServiceResultAwareMethodSecurityExpressionHandlerConfig {

    @Bean
    public DefaultMethodSecurityExpressionHandler getCustomMethodSecurityExpressionHandler(AbstractCustomPermissionEvaluator permissionEvaluator) {
        ServiceResultAwareMethodSecurityExpressionHandler expressionHandler = new ServiceResultAwareMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
