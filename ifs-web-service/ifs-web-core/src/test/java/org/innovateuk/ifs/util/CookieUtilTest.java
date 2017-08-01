package org.innovateuk.ifs.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;
import static org.junit.Assert.*;

public class CookieUtilTest {

    @InjectMocks
    private CookieUtil cookieUtil = new CookieUtil();

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;

    private TextEncryptor encryptor;

    @Before
    public void init () {
        MockitoAnnotations.initMocks(this);

        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        encryptor = Encryptors.text("mysecretpassword", "109240124012412412");

        ReflectionTestUtils.setField(cookieUtil, "cookieSecure", TRUE);
        ReflectionTestUtils.setField(cookieUtil, "cookieHttpOnly", FALSE);
        ReflectionTestUtils.setField(cookieUtil, "encryptionPassword", "mysecretpassword");
        ReflectionTestUtils.setField(cookieUtil, "encryptionSalt", "109240124012412412");
        ReflectionTestUtils.setField(cookieUtil, "encryptor", encryptor);
    }

    @Test
    public void saveToCookie() throws Exception {
        String fieldName =  "cookie_fieldname";
        String value =  "cookieValue";

        cookieUtil.saveToCookie(response, fieldName, value);

        assertEquals(1, ((MockHttpServletResponse) response).getCookies().length);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie(fieldName);
        assertNotEquals(null, cookie);
        assertEquals(value, encryptor.decrypt(cookie.getValue()));
        assertTrue(0 < cookie.getMaxAge());
        assertTrue(cookie.getSecure());
    }

    @Test
    public void saveToCompressedCookie() throws Exception {
        String fieldName =  "cookie_fieldname";
        String value =  "cookieValue";

        cookieUtil.saveToCompressedCookie(response, fieldName, value);

        assertEquals(1, ((MockHttpServletResponse) response).getCookies().length);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie(fieldName);
        assertNotEquals(null, cookie);
        assertTrue(0 < cookie.getMaxAge());
        assertEquals(value, getDecompressedString(cookie.getValue()));
    }

    @Test
    public void removeCookie() throws Exception {
        String fieldName = "cookie_fieldname";

        cookieUtil.removeCookie(response, fieldName);

        assertEquals(1, ((MockHttpServletResponse) response).getCookies().length);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie(fieldName);
        assertEquals(0, cookie.getMaxAge());
        assertTrue(cookie.getSecure());
    }

    @Test
    public void getCookie() throws Exception {
        String fieldName = "cookie_fieldname";
        String value =  "cookieValue";

        Optional<Cookie> cookie = cookieUtil.getCookie(request, fieldName);

        assertEquals(FALSE, cookie.isPresent());

        ((MockHttpServletRequest) request).setCookies(new Cookie(fieldName, value));

        cookie = cookieUtil.getCookie(request, fieldName);

        assertEquals(TRUE, cookie.isPresent());
        assertEquals(value, cookie.get().getValue());
    }

    @Test
    public void getCookieValueWithMissingCookie() throws Exception {
        String fieldName = "cookie_fieldname";

        String cookieValue = cookieUtil.getCookieValue(request, fieldName);

        assertTrue(cookieValue.isEmpty());
    }

    @Test
    public void getCookieValue() throws Exception {
        String fieldName = "cookie_fieldname";
        String value =  "cookieValue";

        ((MockHttpServletRequest) request).setCookies(new Cookie(fieldName, encryptor.encrypt(value)));

        String cookieValue = cookieUtil.getCookieValue(request, fieldName);

        assertEquals(value, cookieValue);
    }

    @Test
    public void getCompressedCookieValue() throws Exception {
        String fieldName = "cookie_fieldname";
        String value =  "cookieValue";

        String content = getCompressedString(value);
        ((MockHttpServletRequest) request).setCookies(new Cookie(fieldName, content));
        String cookieValue = cookieUtil.getCompressedCookieValue(request, fieldName);

        assertEquals(value, cookieValue);
    }
}