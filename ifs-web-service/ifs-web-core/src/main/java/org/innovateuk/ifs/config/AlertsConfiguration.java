package org.innovateuk.ifs.config;

import org.innovateuk.ifs.controller.LoggedInUserMethodArgumentResolver;
import org.innovateuk.ifs.controller.ValidationHandlerMethodArgumentResolver;
import org.innovateuk.ifs.interceptors.AlertMessageHandlerInterceptor;
import org.innovateuk.ifs.interceptors.GoogleAnalyticsHandlerInterceptor;
import org.innovateuk.ifs.interceptors.MenuLinksHandlerInterceptor;
import org.innovateuk.ifs.invite.formatter.RejectionReasonFormatter;
import org.innovateuk.ifs.user.formatter.EthnicityFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.List;
import java.util.Locale;

@Configuration
@ConditionalOnProperty(name = "ifs.web.alertMessagesEnabled", havingValue = "true", matchIfMissing = true)
public class AlertsConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);

        registry.addInterceptor(getAlertMessageHandlerInterceptor());
    }

    @Bean
    public HandlerInterceptor getAlertMessageHandlerInterceptor() {
        return new AlertMessageHandlerInterceptor();
    }
}
