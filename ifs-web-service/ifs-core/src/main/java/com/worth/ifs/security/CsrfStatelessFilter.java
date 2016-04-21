package com.worth.ifs.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * TODO
 * Variation of the default Spring Security {@link org.springframework.security.web.csrf.CsrfFilter}.
 * Relies on the Spring annotation and th:action, and the right request attribute names to get the CSRF token into the Thymeleaf form.
 */
@Service
final class CsrfStatelessFilter extends OncePerRequestFilter {

    private static final String CSRF_COOKIE_NAME = "CSRF-TOKEN";

    private static final Log LOG = LogFactory.getLog(CsrfStatelessFilter.class);

    @Autowired
    private CsrfTokenUtility tokenUtility;

    private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();
    private final RequestMatcher requireCsrfProtectionMatcher = new DefaultRequiresCsrfMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final CsrfToken token = tokenUtility.generateToken();

        // add the CsrfToken as an attribute of the request as expected by link org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor
        request.setAttribute(CsrfToken.class.getName(), token);

        // TODO
        request.setAttribute(token.getParameterName(), token);

        setTokenAsCookie(response, token);

        // check if CSRF protection should be applied to the request
        if (!requireCsrfProtectionMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // validate the CSRF token
        try {
            tokenUtility.validateToken(request);
        } catch (final CsrfException e) {
            LOG.warn("Handling access denied for exception", e);
            accessDeniedHandler.handle(request, response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setTokenAsCookie(final HttpServletResponse response, final CsrfToken token) {
        response.addCookie(createCookie(token));
    }

    private Cookie createCookie(final CsrfToken token) {
        final Cookie cookie = new Cookie(CSRF_COOKIE_NAME, token.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);
        cookie.setSecure(true);
        return cookie;
    }

    protected void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {
        private final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

        @Override
        public boolean matches(HttpServletRequest request) {
            return !allowedMethods.matcher(request.getMethod()).matches();

        }
    }
}
