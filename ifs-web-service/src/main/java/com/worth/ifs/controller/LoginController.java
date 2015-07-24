package com.worth.ifs.controller;

import com.worth.ifs.filter.CsrfHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class LoginController extends WebMvcConfigurerAdapter {
    @RequestMapping("/login")
    public String login(@RequestParam(value="name", required=false, defaultValue="World..") String name, Model model) {
        System.out.println("Login request handler");

        // get all users for login dropdown.
        // Code for calling the rest services.
        //RestTemplate restTemplate = new RestTemplate();
        //User user = restTemplate.getForObject("http://localhost:8090/user/1", User.class);

        return "login";
    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {
        @Autowired
        private SecurityProperties security;

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .authorizeRequests()
                    .antMatchers("/css/**", "/images/**", "/js/**")
                        .permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                        .loginPage("/login")
                        .permitAll()
                    .and()
                    .logout()
                        .permitAll()
                    .and()
                    .exceptionHandling()
                    .accessDeniedPage("/access?error")
                    .and()
                    .csrf().csrfTokenRepository(csrfTokenRepository())
                    .and()
                    .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
        }

        private CsrfTokenRepository csrfTokenRepository() {
            HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
            repository.setHeaderName("X-XSRF-TOKEN");
            return repository;
        }
    }

}
