package com.worth.ifs.util;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static java.lang.String.join;
import static java.util.Collections.nCopies;
import static org.junit.Assert.assertEquals;

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
        final String expected = "~" + requestURI + "?" + queryString;

        final MockHttpServletRequest request = new MockHttpServletRequest(null, requestURI);
        request.setQueryString(queryString);

        assertEquals(expected, thymeleafUtil.uriWithQueryString(request));
    }

    @Test(expected = IllegalArgumentException.class)
    public void uriWithQueryString_null() throws Exception {
        thymeleafUtil.uriWithQueryString(null);
    }

    @Test
    public void uriWithQueryString_noQueryString() throws Exception {
        final String reqeuestURI = "/application/1/form";

        final MockHttpServletRequest request = new MockHttpServletRequest(null, reqeuestURI);

        assertEquals("~" + reqeuestURI, thymeleafUtil.uriWithQueryString(request));
    }

    @Test
    public void wordsRemaining() throws Exception {
        assertEquals(85, thymeleafUtil.wordsRemaining(100, join(" ", nCopies(15, "content"))));
    }

    @Test
    public void wordsRemaining_valueWithHtml() throws Exception {
        assertEquals(85, thymeleafUtil.wordsRemaining(100, "<td><p style=\"font-variant: small-caps\">This value is made up of fifteen words even though it is wrapped within HTML.</p></td>"));
    }

    @Test
    public void wordsRemaining_noMaxWordCount() throws Exception {
        assertEquals(0, thymeleafUtil.wordsRemaining(null, join(" ", nCopies(8, "content"))));
    }

    @Test
    public void wordsRemaining_noContent() throws Exception {
        assertEquals(100, thymeleafUtil.wordsRemaining(100, null));
    }

    @Test
    public void wordsRemaining_emptyContent() throws Exception {
        assertEquals(100, thymeleafUtil.wordsRemaining(100, ""));
    }
}