package com.worth.ifs.util;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class ThymeleafUtilTest {

    private ThymeleafUtil thymeleafUtil;

    @Before
    public void setUp() throws Exception {
        thymeleafUtil = new ThymeleafUtil();
    }

    @Test
    public void uriWithQueryString() throws Exception {
        final String requestURI = "/application/1/form";
        final String queryString = "test=true,newApplication=true";
        final String expected = requestURI + "?" + queryString;

        final MockHttpServletRequest request = new MockHttpServletRequest(null, requestURI);
        request.setQueryString(queryString);

        Assert.assertEquals(expected, thymeleafUtil.uriWithQueryString(request));
    }

    @Test(expected = IllegalArgumentException.class)
    public void uriWithQueryString_null() throws Exception {
        thymeleafUtil.uriWithQueryString(null);
    }

    @Test
    public void uriWithQueryString_noQueryString() throws Exception {
        final String reqeuestURI = "/application/1/form";

        final MockHttpServletRequest request = new MockHttpServletRequest(null, reqeuestURI);

        Assert.assertEquals(reqeuestURI, thymeleafUtil.uriWithQueryString(request));
    }
}