package org.innovateuk.ifs.config.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class AuthenticationFilterTest {

    @Before
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void doFilter() throws Exception {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        TokenAuthenticationService tokenAuthenticationService = mock(TokenAuthenticationService.class);

        when(tokenAuthenticationService.getAuthentication(isA(AuthenticationRequestWrapper.class))).thenReturn
                (authenticationToken);

        AuthenticationFilter authenticationFilter = new AuthenticationFilter("/monitoring", tokenAuthenticationService);

        FilterChain chain = mock(FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        authenticationFilter.doFilter(request, response, chain);

        assertEquals(authenticationToken, SecurityContextHolder.getContext().getAuthentication());
        verify(tokenAuthenticationService).getAuthentication(isA(AuthenticationRequestWrapper.class));
        verify(chain).doFilter(isA(AuthenticationRequestWrapper.class), same(response));
    }

    @Test
    public void doFilter_monitoring() throws Exception {
        TokenAuthenticationService tokenAuthenticationService = mock(TokenAuthenticationService.class);
        AuthenticationFilter authenticationFilter = new AuthenticationFilter("/monitoring", tokenAuthenticationService);

        FilterChain chain = mock(FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/monitoring/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        authenticationFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenAuthenticationService, never()).getAuthentication(isA(AuthenticationRequestWrapper.class));
        verify(chain).doFilter(isA(AuthenticationRequestWrapper.class), same(response));
    }

    @Test
    public void doFilter_notAuthenticated() throws Exception {
        TokenAuthenticationService tokenAuthenticationService = mock(TokenAuthenticationService.class);
        AuthenticationFilter authenticationFilter = new AuthenticationFilter("/monitoring", tokenAuthenticationService);

        FilterChain chain = mock(FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        authenticationFilter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenAuthenticationService).getAuthentication(isA(AuthenticationRequestWrapper.class));
        verify(chain).doFilter(isA(AuthenticationRequestWrapper.class), same(response));
    }
}