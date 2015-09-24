package com.worth.ifs.configuration;

import com.worth.ifs.interceptors.MenuLinksHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class IFSWebConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(getMenuLinksHandlerInterceptor());
    }

    @Bean
    public HandlerInterceptor getMenuLinksHandlerInterceptor() {
        return new MenuLinksHandlerInterceptor();
    }
}
