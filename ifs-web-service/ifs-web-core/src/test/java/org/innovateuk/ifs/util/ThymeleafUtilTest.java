package org.innovateuk.ifs.util;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static java.lang.String.join;
import static java.util.Collections.nCopies;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThymeleafUtilTest {

    private ThymeleafUtil thymeleafUtil;

    @Before
    public void setUp() throws Exception {
        thymeleafUtil = new ThymeleafUtil();
    }

    @Test
    public void formPostUri() throws Exception {
        final String servletPath = "/application/1/form";
        final String queryString = "test=true,newApplication=true";

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn(servletPath);
        when(request.getQueryString()).thenReturn(queryString);
        assertEquals(servletPath, thymeleafUtil.formPostUri(request));
    }

    @Test(expected = IllegalArgumentException.class)
    public void formPostUri_null() throws Exception {
        thymeleafUtil.formPostUri(null);
    }

    @Test
    public void formPostUri_noQueryString() throws Exception {
        final String servletPath = "/application/1/form";

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn(servletPath);


        assertEquals(servletPath, thymeleafUtil.formPostUri(request));
    }

    @Test
    public void wordsRemaining() throws Exception {
        assertEquals(85, thymeleafUtil.wordsRemaining(100, join(" ", nCopies(15, "content"))));
    }

    @Test
    public void wordsRemaining_greaterThanMaxWordCount() throws Exception {
        assertEquals(-15, thymeleafUtil.wordsRemaining(100, join(" ", nCopies(115, "content"))));
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
