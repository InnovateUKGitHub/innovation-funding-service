package com.worth.ifs.commons.security;

import com.worth.ifs.user.domain.User;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * This interface contains the most important methods we need for authentication.
 */
public interface UserAuthenticationService {

    Authentication getAuthentication(HttpServletRequest request);

    User getAuthenticatedUser(HttpServletRequest request);
}
