package com.worth.ifs.util;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Configurable
public final class CookieUtil {
    private CookieUtil(){}

    @Value("${server.session.cookie.secure}")
    private static boolean cookieSecure;

    @Value("${server.session.cookie.http-only}")
    private static boolean cookieHttpOnly;

    public static void saveToCookie(HttpServletResponse response, String fieldName, String fieldValue) {
        if (StringUtils.hasText(fieldName)){
            Cookie cookie = new Cookie(fieldName, fieldValue);
            cookie.setSecure(cookieSecure);
            cookie.setHttpOnly(cookieHttpOnly);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);
        }
    }

    public static void removeCookie(HttpServletResponse response, String fieldName) {
        if (StringUtils.hasText(fieldName)) {
            Cookie cookie = new Cookie(fieldName, "");
            cookie.setSecure(cookieSecure);
            cookie.setHttpOnly(cookieHttpOnly);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    public static Optional<Cookie> getCookie(HttpServletRequest request, String fieldName) {
        return Optional.ofNullable(WebUtils.getCookie(request, fieldName));
    }

    public static String getCookieValue(HttpServletRequest request, String fieldName){
        Optional<Cookie> cookie = getCookie(request, fieldName);
        if(cookie.isPresent()){
            return cookie.get().getValue();
        }
        return "";
    }

}
