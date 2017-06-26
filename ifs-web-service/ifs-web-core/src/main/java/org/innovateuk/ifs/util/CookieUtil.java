package org.innovateuk.ifs.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
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

import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;

@Service
@Configurable
public class CookieUtil {
    private static final Log LOG = LogFactory.getLog(CookieUtil.class);

    private static final Integer COOKIE_LIFETIME = 3600;

    private TextEncryptor encryptor;

    @Value("${server.session.cookie.secure}")
    private Boolean cookieSecure;

    @Value("${server.session.cookie.http-only}")
    private Boolean cookieHttpOnly;

    @Value("${ifs.web.security.csrf.encryption.password}")
    private String encryptionPassword;

    @Value("${ifs.web.security.csrf.encryption.salt}")
    private String encryptionSalt;

    @PostConstruct
    public void init() {
        encryptor = Encryptors.text(encryptionPassword, encryptionSalt);
    }

    public void saveToCookie(HttpServletResponse response, String fieldName, String fieldValue) {
        if (StringUtils.hasText(fieldName)) {
            Cookie cookie;
            try {
                cookie = new Cookie(fieldName, encodeCookieValue(fieldValue));
            } catch (UnsupportedEncodingException e) {
                LOG.error(e);
                return;
            }
            cookie.setSecure(cookieSecure);
            cookie.setHttpOnly(cookieHttpOnly);
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_LIFETIME);
            response.addCookie(cookie);
        }
    }

    public void saveToCompressedCookie(HttpServletResponse response, String fieldName, String fieldValue) {
        if (StringUtils.hasText(fieldName)) {
            String content = getCompressedString(fieldValue);
            Cookie cookie = new Cookie(fieldName, content);
            cookie.setHttpOnly(true);
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

    public Optional<Cookie> getCookie(HttpServletRequest request, String fieldName) {
        return Optional.ofNullable(WebUtils.getCookie(request, fieldName));
    }

    public String getCookieValue(HttpServletRequest request, String cookieName) {
        Optional<Cookie> cookie = getCookie(request, cookieName);
        if (cookie.isPresent()) {
            try {
                return decodeCookieValue(cookie.get().getValue());
            } catch (UnsupportedEncodingException | ArrayIndexOutOfBoundsException ignore) {
                LOG.error("Failing cookie (" + cookieName + "):" + ignore.getMessage());
            }
        }
        return "";
    }

    public String getCompressedCookieValue(HttpServletRequest request, String cookieName) {
        Optional<Cookie> cookie = getCookie(request, cookieName);
        if (cookie.isPresent()) {
            return getDecompressedString(cookie.get().getValue());
        }
        return "";
    }

    public <T> Optional<T> getCookieAs(HttpServletRequest request, String cookieName, TypeReference<T> cookieType) {
        String jsonValue = getCookieValue(request, cookieName);

        if (jsonValue != null && !"".equals(jsonValue)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return Optional.of(mapper.readValue(jsonValue, cookieType));
            } catch (IOException e) {
                //ignored
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
            } catch (IOException e) {
                //ignored
            }
        }
        return new ArrayList<>();
    }

    private String encodeCookieValue(String value) throws UnsupportedEncodingException {
        return encryptor.encrypt(URLEncoder.encode(value, CharEncoding.UTF_8));
    }

    private String decodeCookieValue(String encodedValue) throws UnsupportedEncodingException {
        String decodedValue = "";

        if(encodedValue.trim().length() > 0) {
            decodedValue = URLDecoder.decode(encryptor.decrypt(encodedValue), CharEncoding.UTF_8);
        }

        return decodedValue;
    }
}
