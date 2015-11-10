package com.worth.ifs.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class IfsHttpUtilsTest {

    @Test
    public void test_requestParameterPresent_notPresent() {
        assertEquals(Optional.empty(), IfsHttpUtils.requestParameterPresent("testParameter", new MockHttpServletRequest()));
    }

    @Test
    public void test_requestParameterPresent_present() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", "param value");
        assertEquals(Optional.of("param value"), IfsHttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void test_requestParameterPresent_presentButBlank() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", "");
        assertEquals(Optional.of(""), IfsHttpUtils.requestParameterPresent("testParameter", request));
    }

    @Test
    public void test_requestParameterPresent_presentButNull() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testParameter", (String) null);
        assertEquals(Optional.ofNullable(null), IfsHttpUtils.requestParameterPresent("testParameter", request));
    }

}
