package com.worth.ifs.configuration;

import com.worth.ifs.interceptors.MenuLinksHandlerInterceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.VersionResourceResolver;

@Configuration
public class IFSWebConfiguration extends WebMvcConfigurerAdapter {
    private static final Log LOG = LogFactory.getLog(IFSWebConfiguration.class);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(getMenuLinksHandlerInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        VersionResourceResolver versionResourceResolver = new VersionResourceResolver()
                .addVersionStrategy(new ContentVersionStrategy(), "/**");

        registry.addResourceHandler("/js/**", "/css/**", "/images/**")
                .addResourceLocations("classpath:static/js/", "classpath:static/css/", "classpath:static/images/")
                .setCachePeriod(60 * 60 * 24 * 365) /* one year */
                .resourceChain(true)
                .addResolver(versionResourceResolver);

        super.addResourceHandlers(registry);
    }

    @Bean
    public HandlerInterceptor getMenuLinksHandlerInterceptor() {
        return new MenuLinksHandlerInterceptor();
    }
}
