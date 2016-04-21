package com.worth.ifs.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.*;

public class CsrfStatelessFilterTest {

    @InjectMocks
    private final CsrfStatelessFilter filter = new CsrfStatelessFilter();

    @Mock
    private CsrfTokenUtility tokenUtility;

    @Mock
    private FilterChain filterChain;

    @Mock
    private AccessDeniedHandler accessDeniedHandler;

    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();

    private static final CsrfToken CSRF_TOKEN = new DefaultCsrfToken("headerName", "parameterName", "csrfTokenValue");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(tokenUtility.generateToken()).thenReturn(CSRF_TOKEN);
        when(tokenUtility.validateToken(same(request))).thenReturn(true);

        setUpFilter();
    }

    private void setUpFilter() {
        this.filter.setAccessDeniedHandler(accessDeniedHandler);
    }

    @Test
    public void test_doFilterInternal_invalid() throws Exception {
        final MockHttpServletRequest invalidRequest = new MockHttpServletRequest();
        invalidRequest.setMethod(POST.toString());
        final CsrfException expectedException = new CsrfException("Not allowed");
        when(tokenUtility.validateToken(same(invalidRequest))).thenThrow(expectedException);

        filter.doFilterInternal(invalidRequest, response, filterChain);

        verify(accessDeniedHandler, times(1)).handle(invalidRequest, response, expectedException);
        verifyZeroInteractions(filterChain);
    }

    @Test
    public void test_doFilterInternal_post() throws Exception {
        verifyProtected(POST);
    }

    @Test
    public void test_doFilterInternal_put() throws Exception {
        verifyProtected(PUT);
    }

    @Test
    public void test_doFilterInternal_delete() throws Exception {
        verifyProtected(DELETE);
    }

    @Test
    public void test_doFilterInternal_get() throws Exception {
        verifyUnprotected(GET);
    }

    @Test
    public void test_doFilterInternal_head() throws Exception {
        verifyUnprotected(HEAD);
    }

    @Test
    public void test_doFilterInternal_trace() throws Exception {
        verifyUnprotected(TRACE);
    }

    @Test
    public void test_doFilterInternal_options() throws Exception {
        verifyUnprotected(OPTIONS);
    }

    private void verifyProtected(final HttpMethod method) throws Exception {
        request.setMethod(method.name());
        filter.doFilterInternal(request, response, filterChain);

        verify(tokenUtility, times(1)).generateToken();
        verify(tokenUtility, times(1)).validateToken(any(HttpServletRequest.class));
        verify(filterChain, times(1)).doFilter(request, response);
        assertSame(CSRF_TOKEN, request.getAttribute(CsrfToken.class.getName()));
        assertSame(CSRF_TOKEN.getToken(), response.getCookie("CSRF-TOKEN").getValue());
        verifyZeroInteractions(accessDeniedHandler);
    }

    private void verifyUnprotected(final HttpMethod method) throws Exception {
        request.setMethod(method.name());
        filter.doFilterInternal(request, response, filterChain);

        verify(tokenUtility, times(1)).generateToken();
        verify(filterChain, times(1)).doFilter(request, response);
        verify(tokenUtility, never()).validateToken(any(HttpServletRequest.class));
        assertSame(CSRF_TOKEN, request.getAttribute(CsrfToken.class.getName()));
        assertSame(CSRF_TOKEN.getToken(), response.getCookie("CSRF-TOKEN").getValue());
        verifyZeroInteractions(accessDeniedHandler);
    }
}