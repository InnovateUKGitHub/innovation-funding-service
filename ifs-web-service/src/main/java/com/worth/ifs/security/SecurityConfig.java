package com.worth.ifs.security;

import com.worth.ifs.commons.security.StatelessAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

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

    public SecurityConfig() {
        super(true);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
                .anonymous()
            .and()
                .authorizeRequests()
                // allow anonymous resource requests
                .requestMatchers(statelessAuthenticationFilter.getIgnoredRequestMatchers()).permitAll()
                .anyRequest().authenticated()
//            .and()
//                .logout().deleteCookies(TokenAuthenticationService.AUTH_TOKEN)
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .and()
                .exceptionHandling()
            .and()
                .headers()
                    .frameOptions().sameOrigin()
                    .cacheControl();


        //.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

}
