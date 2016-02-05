package com.worth.ifs.service;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Configurable
public class CookieService {

    @Value("${server.session.cookie.secure}")
    private boolean cookieSecure;

    @Value("${server.session.cookie.http-only}")
    private boolean cookieHttpOnly;

    public void saveToCookie(HttpServletResponse response, String fieldName, String fieldValue) {
        if (fieldName != null) {
            Cookie cookie = new Cookie(fieldName, fieldValue);
            cookie.setSecure(cookieSecure);
            cookie.setHttpOnly(cookieHttpOnly);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);
        }
    }

    public Optional<Cookie> getCookie(HttpServletRequest request, String fieldName) {
        return Optional.ofNullable(WebUtils.getCookie(request, fieldName));
    }

    public String getCookieValue(HttpServletRequest request, String fieldName){
        Optional<Cookie> cookie = this.getCookie(request, fieldName);
        if(cookie.isPresent()){
            return cookie.get().getValue();
        }
        return "";
    }

}
