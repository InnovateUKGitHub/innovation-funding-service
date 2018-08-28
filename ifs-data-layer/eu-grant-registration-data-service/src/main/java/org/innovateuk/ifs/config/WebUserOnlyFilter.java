package org.innovateuk.ifs.config;

import org.innovateuk.ifs.commons.security.UidSupplier;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Arrays.asList;

/**
 * Filter to only allow web system user.
 */
@Service
@Configurable
public class WebUserOnlyFilter extends OncePerRequestFilter {

    @Autowired
    private UidSupplier uidSupplier;

    @Value("${ifs.web.system.user.uid}")
    private String webUserId;

    private static UserResource webUser;

    static {
        webUser = new UserResource();
        webUser.setRoles(asList(Role.SYSTEM_REGISTRATION_USER));
        webUser.setEmail("ifs_web_user@innovateuk.org");
        webUser.setFirstName("IFS Web");
        webUser.setLastName("System User");
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String uid = uidSupplier.getUid(request);
        if (webUserId.equals(uid)) {
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(webUser));
        }
        filterChain.doFilter(request, response);
    }
}
