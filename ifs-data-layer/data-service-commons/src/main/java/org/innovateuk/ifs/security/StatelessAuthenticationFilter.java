package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatelessAuthenticationFilter extends OncePerRequestFilter {

    private UserAuthenticationService userAuthenticationService;

    public StatelessAuthenticationFilter(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        Authentication authentication = userAuthenticationService.getAuthentication(request);
        if(authentication!=null){
            UserResource ur = userAuthenticationService.getAuthenticatedUser(request);
            if (ur == null)  {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else if (ur.getStatus().equals(UserStatus.INACTIVE)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not activated.");
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
