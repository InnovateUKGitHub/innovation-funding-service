package org.innovateuk.ifs.starters.stubdev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
@Order(1)
@Profile("dev")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public SecurityConfig() {
        super(true);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests(
                (authorizeRequests) -> authorizeRequests.antMatchers("/**").permitAll()
        )
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .anonymous().and()
            .exceptionHandling().and()
            .headers()
                .addHeaderWriter(new StaticHeadersWriter("server","server"))
                    .cacheControl().disable();
    }
}
