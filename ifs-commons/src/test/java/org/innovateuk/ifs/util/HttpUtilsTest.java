package org.innovateuk.ifs.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class HttpUtilsTest {

    @Test
    public void requestParameterPresent_notPresent() {
        assertEquals(Optional.empty(), HttpUtils.requestParameterPresent("testParameter", new MockHttpServletRequest()));
    }

    @Test
    public void requestParameterPresent_present() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", "param value");
        assertEquals(Optional.of("param value"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void requestParameterPresent_presentButBlank() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", "");
        assertEquals(Optional.of(""), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void requestParameterPresent_presentButNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", (String) null);
        assertEquals(Optional.empty(), HttpUtils.requestParameterPresent("testParameter", request));
    }


    @Test
    public void MMYYYYrequestParameterPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "12");
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "2011");
        assertEquals(Optional.of("12-2011"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void MMYYYYrequestParameterMonthMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "2011");
        assertEquals(Optional.of("-2011"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void MMYYYYrequestParameterYearMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "12");
        assertEquals(Optional.of("12-"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void getFullRequestUrl() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        request.setQueryString("param=value");
        assertEquals("http://localhost/test?param=value", HttpUtils.getFullRequestUrl(request));
    }

    @Test
    public void getFullRequestUrl_noQuery() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        assertEquals("http://localhost/test", HttpUtils.getFullRequestUrl(request));
    }
}
