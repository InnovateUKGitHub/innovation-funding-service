package com.worth.ifs.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@Component("LoginFilter")
public class LoginFilter extends OncePerRequestFilter {
    protected static String IFS_AUTH_COOKIE_NAME = "IFS_authToken";


    /**
     * Redirect the request to the login page when the user is has no auth token.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {



        chain.doFilter(request, response);
    }

}
