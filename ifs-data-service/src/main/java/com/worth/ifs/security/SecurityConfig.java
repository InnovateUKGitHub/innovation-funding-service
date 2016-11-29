package com.worth.ifs.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .anonymous()
            .and()
                .authorizeRequests()
                // allow anonymous resource requests
                .requestMatchers(statelessAuthenticationFilter.getIgnoredRequestMatchers()).permitAll()
                .antMatchers("/user/uid/*").permitAll()
                .antMatchers("/user/verifyEmail/*").permitAll()
                .antMatchers("/user/createLeadApplicantForOrganisation/*").permitAll()
                .antMatchers("/user/findByEmail/*/").permitAll()
                .antMatchers("/organisation/findById/*").permitAll()
                .antMatchers("/address/doLookup/**").permitAll()
                .antMatchers("/alert/findAllVisible").permitAll()
                .antMatchers("/alert/findAllVisible/*").permitAll()
                .antMatchers("/browser/**").permitAll()
                .antMatchers("/idpstub/**").permitAll()
                .antMatchers("/health").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .and()
                .addFilterBefore(statelessAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers().cacheControl();
    }


}
