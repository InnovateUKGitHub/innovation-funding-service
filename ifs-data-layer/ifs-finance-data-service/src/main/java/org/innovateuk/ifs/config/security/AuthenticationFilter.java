package org.innovateuk.ifs.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authenticates requests using the {@link TokenAuthenticationService}, setting an authentication principal on the
 * security context if the request is authenticated.
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private String monitoringEndpoint;

    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public AuthenticationFilter(@Value("${management.contextPath}") String monitoringEndpoint,
                                TokenAuthenticationService tokenAuthenticationService) {
        this.monitoringEndpoint = monitoringEndpoint;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        AuthenticationRequestWrapper requestWrapper = new AuthenticationRequestWrapper(request);
        if (!request.getRequestURI().startsWith(monitoringEndpoint)) {
            SecurityContextHolder.getContext().setAuthentication(tokenAuthenticationService.getAuthentication
                    (requestWrapper));
        }
        filterChain.doFilter(requestWrapper, response);
    }
}
