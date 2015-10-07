package com.worth.ifs.security;

import com.worth.ifs.user.domain.User;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface contains the most important methods we need for authentication.
 */
public interface UserAuthenticationService {

    public User authenticate(String emailAddress, String password);

    public void addAuthentication(HttpServletResponse response, User user);

    public Authentication getAuthentication(HttpServletRequest request);

    public User getAuthenticatedUser(HttpServletRequest request);

    public void removeAuthentication(HttpServletResponse response);

}
