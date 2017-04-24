package org.innovateuk.ifs.shibboleth.api.security;

import org.innovateuk.ifs.shibboleth.api.ApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private ApiProperties apiProperties;
    private AuthenticationEntryPoint unauthorizedEntryPoint = (request, response, exception) -> {

        LOG.warn("Authentication Exception: {}", exception.getMessage());
        LOG.debug("Authentication Exception - Stacktrace", exception);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    };


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.
            csrf().disable().

            anonymous().disable().

            exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint).and().

            servletApi().and().

            sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().

            authorizeRequests().anyRequest().fullyAuthenticated().and().

            authenticationProvider(preAuthProvider()).

            addFilterBefore(preAuthHeaderFilter(), RequestHeaderAuthenticationFilter.class).

            headers().cacheControl();
    }


    @Override
    public void configure(final AuthenticationManagerBuilder builder) throws Exception {

        builder.authenticationProvider(preAuthProvider());

    }


    private PreAuthenticatedAuthenticationProvider preAuthProvider() {
        final PreAuthenticatedAuthenticationProvider preAuthAuthProvider = new PreAuthenticatedAuthenticationProvider();

        preAuthAuthProvider.setPreAuthenticatedUserDetailsService(
            new PreAuthenticatedGrantedAuthoritiesUserDetailsService()
        );

        return preAuthAuthProvider;
    }


    private PreAuthHeaderAuthenticationFilter preAuthHeaderFilter() throws Exception {

        final PreAuthHeaderAuthenticationFilter filter = new PreAuthHeaderAuthenticationFilter(apiProperties);

        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }
}