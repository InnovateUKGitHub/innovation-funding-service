package com.worth.ifs.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.worth.ifs.commons.security.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

@Service
@Configurable
public class StatelessAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if(shouldBeAuthenticated(httpRequest)) {
            Authentication authentication = userAuthenticationService.getAuthentication(httpRequest);

            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private static boolean shouldBeAuthenticated(final HttpServletRequest httpRequest) {
        String uri = httpRequest.getRequestURI();
        return !(
            uri.contains("/js/") ||
            uri.contains("/css/") ||
            uri.contains("/images/") ||
            uri.contains("/favicon.ico") ||
            uri.contains("/prototypes") ||
            uri.contains("/error") ||
            uri.contains("/jolokia/")
        );
    }
}
