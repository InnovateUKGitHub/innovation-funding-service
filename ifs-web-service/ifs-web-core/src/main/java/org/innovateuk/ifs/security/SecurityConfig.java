package org.innovateuk.ifs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

/**
 * Every request is stateless and is checked if the user has access to requested resource.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CsrfStatelessFilter csrfStatelessFilter;

    @Autowired
    private StatelessAuthenticationFilter statelessAuthenticationFilter;

    public SecurityConfig() {
        super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(csrfStatelessFilter, StatelessAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .anonymous().and()
            .exceptionHandling().and()
            .headers()
                .addHeaderWriter(new StaticHeadersWriter("server","server"))
                    .cacheControl().disable();
    }
}
