package org.innovateuk.ifs.config;

import org.innovateuk.ifs.controller.LoggedInUserMethodArgumentResolver;
import org.innovateuk.ifs.controller.ValidationHandlerMethodArgumentResolver;
import org.innovateuk.ifs.interceptors.AlertMessageHandlerInterceptor;
import org.innovateuk.ifs.interceptors.GoogleAnalyticsHandlerInterceptor;
import org.innovateuk.ifs.interceptors.MenuLinksHandlerInterceptor;
import org.innovateuk.ifs.invite.formatter.RejectionReasonFormatter;
import org.innovateuk.ifs.user.formatter.EthnicityFormatter;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IFSWebConfiguration extends WebMvcConfigurerAdapter {
    public static final int CACHE_PERIOD = 60 * 60 * 24 * 60;

    @Autowired
    Environment env;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(getMenuLinksHandlerInterceptor());
        registry.addInterceptor(getAlertMessageHandlerInterceptor());
        registry.addInterceptor(getGoogleAnalyticsHandlerInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(isCacheResources()) {
            VersionResourceResolver versionResourceResolver = new VersionResourceResolver()
                    .addVersionStrategy(new ContentVersionStrategy(), "/**");

            registry.addResourceHandler("/js/**", "/css/**", "/images/**", "/favicon.ico")
                    .addResourceLocations(
                            "classpath:static/js/", "static/js/",
                            "classpath:static/css/", "static/css/",
                            "classpath:static/images/", "static/images/"
                    )
                    .setCachePeriod(CACHE_PERIOD)
                    .resourceChain(true)
                    .addResolver(versionResourceResolver);
        }else{
            registry.addResourceHandler("/js/**", "/css/**", "/images/**", "/favicon.ico")
                    .addResourceLocations(
                            "classpath:static/js/", "static/js/",
                            "classpath:static/css/", "static/css/",
                            "classpath:static/images/", "static/images/"
                    )
                    .resourceChain(true);
        }
        super.addResourceHandlers(registry);
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(new ValidationHandlerMethodArgumentResolver());
        argumentResolvers.add(getLoggedInUserMethodArgumentResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        super.addFormatters(registry);
        registry.addFormatter(new RejectionReasonFormatter());
        registry.addFormatter(new EthnicityFormatter());
    }

    /**
     * Resources are cached in every environment other than when running locally during development.
     * @return true if resources should be cached, otherwise false.
     */
    private boolean isCacheResources() {
        // All environments except for local development have an active Spring profile of "environment".
        return env.acceptsProfiles("environment");
    }

    public IfSThymeleafDialect getIfsThymeleafDialect() {
        return new IfSThymeleafDialect();
    }

    public IfsThymeleafPostProcessorDialect getIfsThymeleafPostProcessorDialect() {
        return new IfsThymeleafPostProcessorDialect();
    }

    @Bean
    public HandlerInterceptor getMenuLinksHandlerInterceptor() {
        return new MenuLinksHandlerInterceptor();
    }

    @Bean
    public HandlerInterceptor getAlertMessageHandlerInterceptor() {
        return new AlertMessageHandlerInterceptor();
    }

    @Bean
    public HandlerInterceptor getGoogleAnalyticsHandlerInterceptor() {
        return new GoogleAnalyticsHandlerInterceptor();
    }

    @Bean
    public LoggedInUserMethodArgumentResolver getLoggedInUserMethodArgumentResolver() {
        return new LoggedInUserMethodArgumentResolver();
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.UK);
        return slr;
    }
}
