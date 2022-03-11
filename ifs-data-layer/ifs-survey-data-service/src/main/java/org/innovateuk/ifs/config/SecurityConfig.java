package org.innovateuk.ifs.config;

import org.innovateuk.ifs.IfsProfileConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
@Profile(IfsProfileConstants.NOT_STUBDEV)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebUserOnlyFilter webUserOnlyFilter;

    public SecurityConfig() {
        super(true);
    }

    @Value("${management.endpoints.web.base-path}")
    private String monitoringEndpoint;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .anonymous().and()
                .authorizeRequests()
                .antMatchers(monitoringEndpoint+"/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().and()
                .addFilterBefore(webUserOnlyFilter, UsernamePasswordAuthenticationFilter.class)
                .headers()
                .addHeaderWriter(new StaticHeadersWriter("server","server"))
                .cacheControl();
    }
}
