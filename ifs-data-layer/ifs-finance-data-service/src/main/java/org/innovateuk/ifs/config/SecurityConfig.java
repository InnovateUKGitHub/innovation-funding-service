package org.innovateuk.ifs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.config.security.AuthenticationFilter;
import org.innovateuk.ifs.rest.IfsAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration for web-level security in the finance data service.
 */
@Configuration
@EnableWebSecurity
@Profile(IfsProfileConstants.NOT_STUBDEV)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${management.endpoints.web.base-path}")
    private String monitoringEndpoint;

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(new IfsAuthenticationEntryPoint(objectMapper)).and()
                .csrf().disable()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(monitoringEndpoint + "/**").permitAll()
                .anyRequest().authenticated();
    }
}
