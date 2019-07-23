package org.innovateuk.ifs.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;
import static org.junit.Assert.*;

public class CompressedCookieServiceTest {

    @InjectMocks
    private CompressedCookieService cookieUtil = new CompressedCookieService();

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;

    @Before
    public void init () {
        MockitoAnnotations.initMocks(this);

        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();

        ReflectionTestUtils.setField(cookieUtil, "cookieSecure", TRUE);
        ReflectionTestUtils.setField(cookieUtil, "cookieHttpOnly", FALSE);
    }


    @Test
    public void saveToCompressedCookie() {
        String fieldName =  "cookie_fieldname";
        String value =  "cookieValue";

        cookieUtil.saveToCookie(response, fieldName, value);

        assertEquals(1, ((MockHttpServletResponse) response).getCookies().length);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie(fieldName);
        assertNotEquals(null, cookie);
        assertTrue(0 < cookie.getMaxAge());
        assertEquals(value, getDecompressedString(cookie.getValue()));
    }


    @Test
    public void getCompressedCookieValue() {
        String fieldName = "cookie_fieldname";
        String value =  "cookieValue";

        String content = getCompressedString(value);
        ((MockHttpServletRequest) request).setCookies(new Cookie(fieldName, content));
        String cookieValue = cookieUtil.getCookieValue(request, fieldName);

        assertEquals(value, cookieValue);
    }
}