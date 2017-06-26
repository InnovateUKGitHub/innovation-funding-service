package org.innovateuk.ifs.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

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
    public void requestParameterPresent_MMYYYY() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "12");
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "2011");
        assertEquals(Optional.of("12-2011"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void requestParameterPresent_MMYYYY_monthMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "2011");
        assertEquals(Optional.of("-2011"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void requestParameterPresent_MMYYYY_yearMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "12");
        assertEquals(Optional.of("12-"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void requestParameterPresent_MMYYYY_monthIsShort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "1");
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "2011");
        assertEquals(Optional.of("01-2011"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void requestParameterPresent_MMYYYY_monthIsLong() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "111");
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "2011");
        assertEquals(Optional.of("111-2011"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void requestParameterPresent_MMYYYY_yearIsShort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "11");
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "201");
        assertEquals(Optional.of("11-0201"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void requestParameterPresent_MMYYYY_yearIsLong() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "11");
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "20111");
        assertEquals(Optional.of("11-20111"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void getQueryStringParameters() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setQueryString("first=a&second=b&second=c");

        MultiValueMap<String, String> map = HttpUtils.getQueryStringParameters(request);
        assertEquals(1, map.get("first").size());
        assertEquals("a", map.get("first").get(0));
        assertEquals(2, map.get("second").size());
        assertEquals("b", map.get("second").get(0));
        assertEquals("c", map.get("second").get(1));
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
