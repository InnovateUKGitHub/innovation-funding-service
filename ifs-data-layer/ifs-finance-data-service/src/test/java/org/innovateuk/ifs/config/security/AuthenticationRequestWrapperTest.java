package org.innovateuk.ifs.config.security;

import com.google.common.io.CharStreams;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletInputStream;
import java.io.InputStreamReader;
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
        try (Reader reader = new InputStreamReader(authenticationRequestWrapper.getInputStream())) {
            assertEquals("First call to getInputStream should match request content",
                    content, CharStreams.toString(reader));
        }

        try (Reader reader = new InputStreamReader(authenticationRequestWrapper.getInputStream())) {
            assertEquals("Subsequent calls to getInputStream should match request content",
                    content, CharStreams.toString(reader));
        }
    }

    @Test
    public void getReader() throws Exception {

        try (Reader reader = authenticationRequestWrapper.getReader()) {
            assertEquals("First call to getReader should match request content",
                    content, CharStreams.toString(reader));
        }

        try (Reader reader = authenticationRequestWrapper.getReader()) {
            assertEquals("Subsequent calls to getReader should match request content",
                    content, CharStreams.toString(reader));
        }

    }
}