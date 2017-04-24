package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
@Configurable
public class StatelessAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Value("management.contextPath")
    private String monitoringEndpoint;

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

    private boolean shouldBeAuthenticated(final HttpServletRequest httpRequest) {
        String uri = httpRequest.getRequestURI();
        return !(
            uri.startsWith(monitoringEndpoint) ||
            uri.startsWith("/js/") ||
            uri.startsWith("/css/") ||
            uri.startsWith("/images/") ||
            uri.equals("/favicon.ico") ||
            uri.startsWith("/prototypes") ||
            uri.startsWith("/error")
        );
    }
}
