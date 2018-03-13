package org.innovateuk.ifs.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthenticationFilter implements Filter {

    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public AuthenticationFilter(final TokenAuthenticationService tokenAuthenticationService) {
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        AuthenticationRequestWrapper requestWrapper = new AuthenticationRequestWrapper(
                (HttpServletRequest) request);
        SecurityContextHolder.getContext().setAuthentication(
                tokenAuthenticationService.getAuthentication(requestWrapper));
        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {
    }
}
