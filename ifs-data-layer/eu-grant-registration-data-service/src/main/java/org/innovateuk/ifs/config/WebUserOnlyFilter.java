package org.innovateuk.ifs.config;

import org.innovateuk.ifs.security.WebUserSecuritySetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to only allow web system user.
 */
@Service
@Configurable
public class WebUserOnlyFilter extends OncePerRequestFilter {

    @Autowired
    private WebUserSecuritySetter webUserSecuritySetter;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (webUserSecuritySetter.isWebUser(request)) {
            webUserSecuritySetter.setWebUser();
        }

        filterChain.doFilter(request, response);
    }
}
