package org.innovateuk.ifs.analytics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuration for the {@link GoogleAnalyticsDataLayerInterceptor}.
 */
@Configuration
public class GoogleAnalyticsDataLayerConfig extends WebMvcConfigurerAdapter {

    @Bean
    public GoogleAnalyticsDataLayerInterceptor googleAnalyticsDataLayerInterceptor() {
        return new GoogleAnalyticsDataLayerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(googleAnalyticsDataLayerInterceptor());
    }
}
