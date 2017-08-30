package org.innovateuk.ifs.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@Service
@Configurable
public class StatelessAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Value("management.contextPath")
    private String monitoringEndpoint;

    @Value("${logout.url}")
    private String logoutUrl;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if(shouldBeAuthenticated(request)) {
            Authentication authentication = userAuthenticationService.getAuthentication(request, true);

            if (authentication != null) {
                UserResource ur = userAuthenticationService.getAuthenticatedUser(request);
                if (ur == null) {
                    response.sendRedirect(logoutUrl);
                } else if (ur.getStatus().equals(UserStatus.INACTIVE)) {
                    response.sendRedirect(logoutUrl);
                } else {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
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
            uri.startsWith("/error") ||
            uri.startsWith("/benchmark/")
        );
    }
}
