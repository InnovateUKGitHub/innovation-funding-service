package org.innovateuk.ifs.security;

import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;


/**
 * Spring configuration class to override the default {@link org.springframework.security.access.PermissionEvaluator}
 * with the custom {@link CustomPermissionEvaluator} in our subclass of {@link DefaultMethodSecurityExpressionHandler}
 * the {@link DefaultMethodSecurityExpressionHandler}.
 */
@Configuration
public class ServiceResultAwareMethodSecurityExpressionHandlerConfig {

    @Bean
    public DefaultMethodSecurityExpressionHandler getCustomMethodSecurityExpressionHandler(CustomPermissionEvaluator permissionEvaluator) {
        ServiceResultAwareMethodSecurityExpressionHandler expressionHandler = new ServiceResultAwareMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
