package org.innovateuk.ifs.config.security;

import org.springframework.beans.factory.annotation.Autowired;
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

    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    public AuthenticationFilter(final TokenAuthenticationService tokenAuthenticationService) {
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final
    FilterChain filterChain) throws ServletException, IOException {
        AuthenticationRequestWrapper requestWrapper = new AuthenticationRequestWrapper(request);

        AuthenticationToken authentication = tokenAuthenticationService.getAuthentication(requestWrapper);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(
                    tokenAuthenticationService.getAuthentication(requestWrapper));
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or no X-AUTH-TOKEN header found in " +
                    "request");
        }

        filterChain.doFilter(requestWrapper, response);
    }
}
