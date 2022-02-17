package org.innovateuk.ifs.starters.stubdev.security;

import org.innovateuk.ifs.IfsProfileConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile(IfsProfileConstants.STUBDEV)
public class StubSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    public StubAuthFilter stubAuthFilter;

    public StubSecurityConfig() {
        super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests(
                (authorizeRequests) -> authorizeRequests.antMatchers("/**").permitAll()
        )
            .csrf().disable()
                .addFilterBefore(stubAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .anonymous().and()
            .exceptionHandling().and()
            .headers()
                .addHeaderWriter(new StaticHeadersWriter("server","server"))
                    .cacheControl().disable();
    }
}
