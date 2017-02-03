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
    public void test_requestParameterPresent_notPresent() {
        assertEquals(Optional.empty(), HttpUtils.requestParameterPresent("testParameter", new MockHttpServletRequest()));
    }

    @Test
    public void test_requestParameterPresent_present() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", "param value");
        assertEquals(Optional.of("param value"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void test_requestParameterPresent_presentButBlank() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", "");
        assertEquals(Optional.of(""), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void test_requestParameterPresent_presentButNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", (String) null);
        assertEquals(Optional.empty(), HttpUtils.requestParameterPresent("testParameter", request));
    }


    @Test
    public void test_MMYYYYrequestParameterPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "12");
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "2011");
        assertEquals(Optional.of("12-2011"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void test_MMYYYYrequestParameterMonthMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_YEAR_APPEND, "2011");
        assertEquals(Optional.of("-2011"), HttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void test_MMYYYYrequestParameterYearMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter" + HttpUtils.MM_YYYY_MONTH_APPEND, "12");
        assertEquals(Optional.of("12-"), HttpUtils.requestParameterPresent("testParameter", request));
    }

}
