package org.innovateuk.ifs.config.security;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletInputStream;
import java.io.Reader;

import static org.junit.Assert.assertEquals;

public class AuthenticationRequestWrapperTest {

    private AuthenticationRequestWrapper authenticationRequestWrapper;

    private static final String content = "content";

    @Before
    public void setUp() throws Exception {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setContent(content.getBytes());

        authenticationRequestWrapper = new AuthenticationRequestWrapper(mockHttpServletRequest);
    }

    @Test
    public void getInputStream() throws Exception {
        ServletInputStream first = authenticationRequestWrapper.getInputStream();
        assertEquals("First call to getInputStream should match request content",
                content, IOUtils.toString(first, "UTF-8"));

        ServletInputStream second = authenticationRequestWrapper.getInputStream();
        assertEquals("Subsequent calls to getInputStream should match request content",
                content, IOUtils.toString(second, "UTF-8"));
    }

    @Test
    public void getReader() throws Exception {
        Reader first = authenticationRequestWrapper.getReader();
        assertEquals("First call to getInputStream should match request content",
                content, IOUtils.toString(first));

        Reader second = authenticationRequestWrapper.getReader();
        assertEquals("Subsequent calls to getInputStream should match request content",
                content, IOUtils.toString(second));
    }
}