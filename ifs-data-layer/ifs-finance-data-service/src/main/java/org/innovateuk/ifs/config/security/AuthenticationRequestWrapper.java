package org.innovateuk.ifs.config.security;

import com.google.common.io.CharStreams;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * {@link HttpServletRequest} wrapper that caches all content read from
 * the {@linkplain #getInputStream() input stream}. It allows multiple calls to
 * {@linkplain #getInputStream() input stream} by returning a new input stream for the cache every time.
 */
public class AuthenticationRequestWrapper extends HttpServletRequestWrapper {

    private String body;

    AuthenticationRequestWrapper(HttpServletRequest request) {
        super(request);
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            body = CharStreams.toString(reader);
        } catch (IOException e) {
            throw new IllegalStateException("Error reading the request payload", e);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(body.getBytes());

        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                throw new IllegalStateException("Not implemented");
            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}