package org.innovateuk.ifs.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;

/**
 * Following a spring upgrade the default error handling in spring-security changed and the previous setup
 * in @see org.innovateuk.ifs.rest.RestErrorController was no longer triggered for auth errors.
 *
 * This recreates that functionality and should be wired in via SecurityConfig as follows -:
 *
 * .exceptionHandling().authenticationEntryPoint(new IfsAuthenticationEntryPoint(objectMapper))
 */
public class IfsAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper;

    public IfsAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().println(objectMapper.writeValueAsString(new RestErrorResponse(forbiddenError())));
    }

}