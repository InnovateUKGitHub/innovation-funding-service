package com.worth.ifs.commons.security;

import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handling the tokens used for authentication for each request
 */
@Service
public class TokenAuthenticationService implements UserAuthenticationService {

    public static final String AUTH_TOKEN = "IFS_AUTH_TOKEN";
    private static final int ONE_DAY = 60 * 60 * 24;

    @Value("${server.session.cookie.secure}")
    private boolean cookieSecure;

    @Value("${server.session.cookie.http-only}")
    private boolean cookieHttpOnly;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Autowired
    private CredentialsValidator credentialsValidator;



    /**
     * Authenticate the user by email address and password
     */
    public User authenticate(String emailAddress, String password) {
        User user = credentialsValidator.retrieveUserByEmailAndPassword(emailAddress, password);
        if ( user != null ) {
            return user;
        } else {
            throw new BadCredentialsException("Invalid username password combination");
        }
    }

    /**
     * Retrieve the Authenticated user by its authentication token in the request header.
     */
    public User getAuthenticatedUser(HttpServletRequest request) {
        Authentication authentication = getAuthentication(request);
        if(authentication!=null) {
            return (User) authentication.getDetails();
        }
        return null;
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        final String token = tokenSupplier.getToken(request);
        User user = credentialsValidator.retrieveUserByToken(token);
        return user != null ? new UserAuthentication(user) : null;
    }

    /**
     * For every request a token needs to be added, which is used for authentication
     */
    private void addAuthentication(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(AUTH_TOKEN, token);
        cookie.setMaxAge(ONE_DAY);
        cookie.setSecure(cookieSecure);
        cookie.setHttpOnly(cookieHttpOnly);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    public void addAuthentication(HttpServletResponse response, User user) {
        this.addAuthentication(response, user.getToken());
    }
    public void addAuthentication(HttpServletResponse response, UserResource user) {
        this.addAuthentication(response, user.getToken());
    }

    public void removeAuthentication(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTH_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setSecure(cookieSecure);
        cookie.setHttpOnly(cookieHttpOnly);
        response.addCookie(cookie);
    }

    public static String getAuthenticationCookieName() {
        return AUTH_TOKEN;
    }
}
