package org.innovateuk.ifs.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract service for handling cookies.
 */
public abstract class AbstractCookieService {
    private static final Log LOG = LogFactory.getLog(AbstractCookieService.class);

    private static final Integer COOKIE_LIFETIME = 3600;

    @Value("${server.servlet.session.cookie.secure}")
    private Boolean cookieSecure;

    @Value("${server.servlet.session.cookie.http-only}")
    private Boolean cookieHttpOnly;

    public Optional<Cookie> getCookie(HttpServletRequest request, String fieldName) {
        return Optional.ofNullable(WebUtils.getCookie(request, fieldName));
    }

    public void saveToCookie(HttpServletResponse response, String fieldName, String fieldValue) {
        if (StringUtils.hasText(fieldName)) {
            Cookie cookie = new Cookie(fieldName, getValueToSave(fieldValue));
            cookie.setSecure(cookieSecure);
            cookie.setHttpOnly(cookieHttpOnly);
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_LIFETIME);
            response.addCookie(cookie);
        }
    }

    public void removeCookie(HttpServletResponse response, String fieldName) {
        if (StringUtils.hasText(fieldName)) {
            Cookie cookie = new Cookie(fieldName, "");
            cookie.setSecure(cookieSecure);
            cookie.setHttpOnly(cookieHttpOnly);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }


    public String getCookieValue(HttpServletRequest request, String cookieName) {
        Optional<Cookie> cookie = getCookie(request, cookieName);
        if (cookie.isPresent()) {
            try {
                return getValueFromCookie(cookie.get().getValue());
            } catch (ArrayIndexOutOfBoundsException ignore) {
                LOG.error("Failing cookie (" + cookieName + "):" + ignore.getMessage(), ignore);
            }
        }
        return "";
    }

    public <T> Optional<T> getCookieAs(HttpServletRequest request, String cookieName, TypeReference<T> cookieType) {
        String jsonValue = getCookieValue(request, cookieName);

        if (StringUtils.hasText(jsonValue)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return Optional.of(mapper.readValue(jsonValue, cookieType));
            } catch (IOException ignored) {
                //ignored
                LOG.trace(ignored);
            }
        }
        return Optional.empty();
    }

    public <T> List<T> getCookieAsList(HttpServletRequest request, String cookieName, TypeReference<List<T>> cookieType) {
        String jsonValue = getCookieValue(request, cookieName);

        if (jsonValue != null && !"".equals(jsonValue)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(jsonValue, cookieType);
            } catch (IOException ignored) {
                //ignored
                LOG.trace(ignored);
            }
        }
        return new ArrayList<>();
    }

    protected String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOG.error(e);
            return "";
        }
    }

    protected String decodeValue(String encodedValue) {
        String decodedValue = "";

        if(encodedValue.trim().length() > 0) {
            try {
                decodedValue = URLDecoder.decode(encodedValue, CharEncoding.UTF_8);
            } catch (UnsupportedEncodingException e) {
                LOG.error(e);
            }
        }

        return decodedValue;
    }

    protected abstract String getValueToSave(String value);
    protected abstract String getValueFromCookie(String value);
}
