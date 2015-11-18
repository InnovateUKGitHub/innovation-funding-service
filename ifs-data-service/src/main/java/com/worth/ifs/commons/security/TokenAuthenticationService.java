package com.worth.ifs.commons.security;

import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.service.UserRestService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handling the tokens used for authentication for each request
 */
@Service
public class TokenAuthenticationService implements UserAuthenticationService {
    private final Log log = LogFactory.getLog(getClass());
    private static final String AUTH_TOKEN = "IFS_AUTH_TOKEN";
    private static final int ONE_DAY = 60 * 60 * 24;

    @Autowired
    UserRestService userRestService;

    /**
     * Authenticate the user by email address and password
     */
    public User authenticate(String emailAddress, String password) {
        User user = userRestService.retrieveUserByEmailAndPassword(emailAddress, password);

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
        final String token = getToken(request);
        if (token != null) {
            // call rest service to obtain user by token
            try {
                User user = userRestService.retrieveUserByToken(token);
                if (user != null) {
                    return new UserAuthentication(user);
                }
            } catch(HttpClientErrorException e) {
                log.error(e);
            }
        }
        return null;
    }

    /**
     * Retrieve the token from the browser cookie, such that it can be
     * used for further requests
     */
    protected String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies!=null) {
            for(Cookie cookie: cookies) {
                if(cookie.getName().equals(AUTH_TOKEN)) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }


    /**
     * For every request a token needs to be added, which is used for authentication
     */
    public void addAuthentication(HttpServletResponse response, User user) {
        Cookie cookie = new Cookie(AUTH_TOKEN, user.getToken());
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setMaxAge(ONE_DAY);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void removeAuthentication(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTH_TOKEN, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }


    public static String getAuthenticationCookieName() {
        return AUTH_TOKEN;
    }
}
