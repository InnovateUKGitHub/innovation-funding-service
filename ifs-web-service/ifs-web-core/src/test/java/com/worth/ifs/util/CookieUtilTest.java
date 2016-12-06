package com.worth.ifs.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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
import static org.junit.Assert.*;

public class CookieUtilTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;

    private TextEncryptor encryptor;

    @Before
    public void init () {
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        encryptor = Encryptors.text("mysecretpassword", "109240124012412412");

        ReflectionTestUtils.setField(CookieUtil.getInstance(), "cookieSecure", TRUE);
        ReflectionTestUtils.setField(CookieUtil.getInstance(), "cookieHttpOnly", FALSE);
        ReflectionTestUtils.setField(CookieUtil.getInstance(), "encryptionPassword", "mysecretpassword");
        ReflectionTestUtils.setField(CookieUtil.getInstance(), "encryptionSalt", "109240124012412412");
        ReflectionTestUtils.setField(CookieUtil.getInstance(), "encryptor", encryptor);
    }

    @Test
    public void getInstance() throws Exception {
        assertEquals(CookieUtilHelper.class, CookieUtil.getInstance().getClass());
        assertNotEquals(null, CookieUtil.getInstance());
    }

    @Test
    public void saveToCookie() throws Exception {
        String fieldName =  "cookie_fieldname";
        String value =  "cookieValue";

        CookieUtil.getInstance().saveToCookie(response, fieldName, value);

        assertEquals(1, ((MockHttpServletResponse) response).getCookies().length);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie(fieldName);
        assertNotEquals(null, cookie);
        assertEquals(value, encryptor.decrypt(cookie.getValue()));
        assertTrue(0 < cookie.getMaxAge());
        assertTrue(cookie.getSecure());
    }

    @Test
    public void removeCookie() throws Exception {
        String fieldName = "cookie_fieldname";

        CookieUtil.getInstance().removeCookie(response, fieldName);

        assertEquals(1, ((MockHttpServletResponse) response).getCookies().length);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie(fieldName);
        assertEquals(0, cookie.getMaxAge());
        assertTrue(cookie.getSecure());
    }

    @Test
    public void getCookie() throws Exception {
        String fieldName = "cookie_fieldname";
        String value =  "cookieValue";

        Optional<Cookie> cookie = CookieUtil.getInstance().getCookie(request, fieldName);

        assertEquals(FALSE, cookie.isPresent());

        ((MockHttpServletRequest) request).setCookies(new Cookie(fieldName, value));

        cookie = CookieUtil.getInstance().getCookie(request, fieldName);

        assertEquals(TRUE, cookie.isPresent());
        assertEquals(value, cookie.get().getValue());
    }

    @Test
    public void getCookieValue() throws Exception {
        String fieldName = "cookie_fieldname";
        String value =  "cookieValue";

        ((MockHttpServletRequest) request).setCookies(new Cookie(fieldName, encryptor.encrypt(value)));

        String cookieValue = CookieUtil.getInstance().getCookieValue(request, fieldName);

        assertEquals(value, cookieValue);
    }
}