package org.innovateuk.ifs.config.security;

import org.innovateuk.ifs.security.HashBasedMacTokenHandler;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TokenAuthenticationServiceTest {

    private TokenAuthenticationService tokenAuthenticationService;

    @Before
    public void setUp() throws Exception {
        String secretKey = "supersecretkey";
        HashBasedMacTokenHandler hashBasedMacTokenHandler = new HashBasedMacTokenHandler();

        tokenAuthenticationService = new TokenAuthenticationService
                (secretKey, hashBasedMacTokenHandler);
    }

    @Test
    public void getAuthentication() throws Exception {
        String token = "f6d99caceac489fd2d4ba8106d15e64bd7455fd83305f13a7faa32fb3b02fa28";

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-AUTH-TOKEN", token);
        httpServletRequest.setContent("input".getBytes());

        assertNotNull(tokenAuthenticationService.getAuthentication(httpServletRequest));
    }

    @Test
    public void getAuthentication_notAuthenticated() throws Exception {
        String token = "incorrect-hash";

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-AUTH-TOKEN", token);
        httpServletRequest.setContent("input".getBytes());

        assertNull(tokenAuthenticationService.getAuthentication(httpServletRequest));
    }

    @Test
    public void getAuthentication_missingContent() throws Exception {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

        assertNull(tokenAuthenticationService.getAuthentication(httpServletRequest));
    }
}