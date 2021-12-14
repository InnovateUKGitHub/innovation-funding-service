package org.innovateuk.ifs.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * <p>
 * Variation of the default Spring Security {@link org.springframework.security.web.csrf.CsrfFilter}. Caters for being a stateless application by employing the Encrypted Token Pattern:
 * <ol>
 *   <li>Client browser sends a request.</li>
 *   <li>Filter generates a new encrypted token using the {@link CsrfTokenService} consisting of a random one-off string (nonce), the UID of the currently authenticated user, and a timestamp.</li>
 *   <li>Token is added into the response as a hidden field of any Thymeleaf forms which can then be passed back on submission.</li>
 *   <li>Token is also added as a cookie to be read by any javascript wanting to make a protected request (e.g. ajax) rather than relying on a form with the hidden field existing in the DOM which won't always be the case.</li>
 *   <li>Client browser sends a protected request invoking a state change (e.g. a request using the POST method)</li>
 *   <li>The protected request includes either the CSRF token in the `X-CSRF-TOKEN header (e.g. for an ajax request), or as the `_csrf` parameter (e.g. when a Thymeleaf form is submitted).</li>
 *   <li>Repeat the initial steps to generate a new token and add it to the response for use in subsequent requests.</li>
 *   <li>{@link CsrfTokenService} decrypts and validates that the token has not expired and was generated for the currently authenticated user.</li>
 *   <li>On failure handle the request with a 403 response, otherwise forward the request to the next filter in the chain.</li>
 * </ol>
 * </p>
 * <p>
 * Developers are required to ensure that {@link CsrfStatelessFilter} is invoked for any request that allows state to change.
 * Typically this just means that they should ensure their web application follows proper REST semantics (i.e. do not change state with the HTTP methods GET, HEAD, TRACE, OPTIONS).
 * </p>
 * <p>
 * When used in combination with the {@link org.springframework.security.config.annotation.web.configuration.EnableWebSecurity} annotation on a {@link org.springframework.context.annotation.Configuration} class,
 * any Thymeleaf form will automatically have the CsrfToken appended by the {@link org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor}.
 * </p>
 * <p>
 * The Thymeleaf CSRF processor relies on the form having a th:action attribute and not the HTML action, so the attribute {@code action="#"} is not sufficient to invoke this.
 * {@code th:action} calls RequestDataValueProcessor.getExtraHiddenFields(...) and adds the returned CSRF token as a hidden field just before the closing </form> tag.
 * </p>
 */
@Service
@Slf4j
final class CsrfStatelessFilter extends OncePerRequestFilter {

    private static final String CSRF_COOKIE_NAME = "CSRF-TOKEN";

    @Autowired
    private CsrfTokenService tokenService;

    private boolean enableDevTools;

    @Value("${ifsEnableDevTools:false}")
    public void setEnableDevTools(boolean enableDevTools) {
        this.enableDevTools = enableDevTools;
    }

    private AccessDeniedHandler accessDeniedHandler;
    private RequestMatcher requireCsrfProtectionMatcher;

    @PostConstruct
    protected void init() {
        accessDeniedHandler = new AccessDeniedHandlerImpl();
        requireCsrfProtectionMatcher = new DefaultRequiresCsrfMatcher(enableDevTools);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Set the token to be used in subsequent requests
        if (!isResourceRequest(request)) {

            final CsrfToken token = tokenService.generateToken();

            // Add the CsrfToken as an attribute of the request as expected by org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor#getExtraHiddenFields(javax.servlet.http.HttpServletRequest)
            request.setAttribute(CsrfToken.class.getName(), token);

            // Not all pages have a Thymeleaf form on them.
            // To cater for javascript clients making ajax calls that need to supply the CSRF token as a header parameter, set the token in the response as a cookie.
            // The cookie can then be the preferred location for finding the current CSRF token in javascript, over relying on a searching for a Thymeleaf form in the DOM.
            setTokenAsCookie(response, token);
        }

        // Check if CSRF protection should be applied to this request
        if (!requireCsrfProtectionMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validate the CSRF token
        try {
            tokenService.validateToken(request);
        } catch (final CsrfException e) {
            log.warn("Handling access denied for exception", e);
            accessDeniedHandler.handle(request, response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isResourceRequest(final HttpServletRequest request) {
        final String uri = request.getRequestURI();
        return uri.startsWith("/js/") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/favicon.ico") ||
                uri.startsWith("/error");
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

    /**
     * Set the AccessDeniedHandler for testing
     */
    protected void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * Initialise the matcher for testing
     */
    protected void initProtectionMatcher() {
        requireCsrfProtectionMatcher = new DefaultRequiresCsrfMatcher(enableDevTools);
    }

    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {

        private boolean enableDevTools;
        private final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

        /**
         * @param enableDevTools Allow Spring Dev Tools to bypass the need for a CSRF token.  This is based on
         *                       partially matching the request URL and is a potential security vulnerability,
         *                       so only enable this on a local development environment.
         */
        public DefaultRequiresCsrfMatcher(boolean enableDevTools) {
            this.enableDevTools = enableDevTools;
        }

        /**
         * @return true if a CSRF token is required for the request method
         */
        @Override
        public boolean matches(HttpServletRequest request) {
            boolean isAllowedMethod = allowedMethods.matcher(request.getMethod()).matches();
            boolean isDevToolsEndpoint = request.getRequestURI().contains("/.~~spring-boot!~");
            return !isAllowedMethod && !(isDevToolsEndpoint && enableDevTools);
        }
    }
}
