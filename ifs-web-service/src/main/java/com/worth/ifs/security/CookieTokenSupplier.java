package com.worth.ifs.security;

import com.worth.ifs.commons.security.TokenSupplier;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;

/**
 *
 */
@Component
public class CookieTokenSupplier implements TokenSupplier {

    @Override
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
}
