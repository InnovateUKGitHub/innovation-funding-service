package org.innovateuk.ifs.security.config;

import org.innovateuk.ifs.security.StatelessAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private StatelessAuthenticationFilter statelessAuthenticationFilter;

    @Value("${management.endpoints.web.base-path}")
    private String monitoringEndpoint;

    private final String[] ADDITIONAL_WHITELIST;

    public SecurityConfig(String[] additionalWhitelist, boolean disableDefaults) {
        super(disableDefaults);
        ADDITIONAL_WHITELIST = additionalWhitelist;
    }

    public SecurityConfig() {
        super(true);
        ADDITIONAL_WHITELIST = new String[]{};
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .anonymous().and()
            .authorizeRequests()
                .antMatchers("/silstub/**").permitAll()
                .antMatchers(monitoringEndpoint+"/**").permitAll()
                .antMatchers("/benchmark/**").permitAll()
                .antMatchers(ADDITIONAL_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
            .exceptionHandling().and()
            .addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers()
                .addHeaderWriter(new StaticHeadersWriter("server","server"))
                .cacheControl();
    }
}
