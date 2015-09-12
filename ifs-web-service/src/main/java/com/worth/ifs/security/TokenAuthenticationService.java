package com.worth.ifs.security;

import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TokenAuthenticationService {
    private final Log log = LogFactory.getLog(getClass());
    private static final String AUTH_TOKEN = "IFS_AUTH_TOKEN";
    private static final int ONE_DAY = 60 * 60 * 24;

    @Autowired
    UserService userService;

    /**
     * Retrieve the Authenticated user by its authentication token in the request header.
     *
     * @param request the servlet request.
     * @return the authenticated user
     */
    public Authentication getAuthentication(HttpServletRequest request) {
        //final String token = request.getHeader(AUTH_HEADER_NAME);
        final String token = getToken(request);
        if (token != null) {
            // call rest service to obtain user by token
            try {
                User user = userService.retrieveUserByToken(token);
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
     * For every request a token needs to be added, which is used for authentication
     *
     * @param response servlet response
     * @param token put this in a cookie for further use
     * @return
     */
    public void addAuthentication(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(AUTH_TOKEN, token);
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

    /**
     * Retrieve the token from the browser cookie, such that it can be
     * used for further requests
     *
     * @param request
     * @return
     */
    public String getToken(HttpServletRequest request) {
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

    public static String getAuthenticationCookieName() {
        return AUTH_TOKEN;
    }
}
