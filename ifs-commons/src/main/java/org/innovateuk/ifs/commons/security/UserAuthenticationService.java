package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.core.Authentication;
import javax.servlet.http.HttpServletRequest;

/**
 * This interface contains the most important methods we need for authentication.
 */
public interface UserAuthenticationService {

    Authentication getAuthentication(HttpServletRequest request);
    UserResource getAuthenticatedUser(HttpServletRequest request);

    Authentication getAuthentication(HttpServletRequest request, boolean expireCache);
    UserResource getAuthenticatedUser(HttpServletRequest request, boolean expireCache);
}
