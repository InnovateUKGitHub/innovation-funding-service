package org.innovateuk.ifs.config;

import org.innovateuk.ifs.interceptors.AlertMessageHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(name = "ifs.web.alertMessagesEnabled", havingValue = "true", matchIfMissing = true)
public class AlertsConfiguration implements WebMvcConfigurer {

    @Autowired
    Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getAlertMessageHandlerInterceptor());
    }

    @Bean
    public HandlerInterceptor getAlertMessageHandlerInterceptor() {
        return new AlertMessageHandlerInterceptor();
    }
}
