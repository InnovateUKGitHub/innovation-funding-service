package com.worth.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Optional;

@Configurable
public final class CookieUtil {
    private CookieUtil(){}
    private static final Log LOG = LogFactory.getLog(CookieUtil.class);

    @Value("${server.session.cookie.secure}")
    private static boolean cookieSecure;

    @Value("${server.session.cookie.http-only}")
    private static boolean cookieHttpOnly;

    public static void saveToCookie(HttpServletResponse response, String fieldName, String fieldValue) {
        if (StringUtils.hasText(fieldName)){
            Cookie cookie = null;
            try {
                cookie = new Cookie(fieldName, URLEncoder.encode(fieldValue, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                LOG.error(e);
                return;
            }
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
            try {
                return URLDecoder.decode(cookie.get().getValue(), "UTF-8");
            } catch (UnsupportedEncodingException ignore) {
                LOG.error(ignore);
                //Do nothing
            }
        }
        return "";
    }

}
