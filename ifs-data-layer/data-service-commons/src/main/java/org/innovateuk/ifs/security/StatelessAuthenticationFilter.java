package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Configurable
public class StatelessAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        Authentication authentication = userAuthenticationService.getAuthentication(httpRequest);
        if(authentication!=null){
            UserResource ur = userAuthenticationService.getAuthenticatedUser(httpRequest);
            if (ur == null)  {
                // avoid leaking information about auth failures
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else if (ur.getStatus().equals(UserStatus.INACTIVE)) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not activated.");
            } else {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
