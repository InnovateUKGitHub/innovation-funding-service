package com.worth.ifs.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Since the default Spring flash messages use sessions and we cannot use session because of the stateless design,
 * we need to use cookies. This class reads the cookies, and if there is a cookie then a new request variable is set to
 * true. The request variable's name is equals to the value of the cookie. After adding the value to the request variables,
 * the cookie is removed (set to "").
 *
 * ATM this is only used for setting a flashMessage on redirection.
 */
@Service
@Configurable
public class CookieFlashMessageFilter extends GenericFilterBean {
    private final Log log = LogFactory.getLog(getClass());
    public static final String COOKIE_NAME = "flashMessage";

    /**
     * Cookie is set, just for after redirecting, so the lifetime should be short.
     */
    public void setFlashMessage(HttpServletResponse response, String name){
        Cookie cookie = new Cookie(COOKIE_NAME, name);
        cookie.setHttpOnly(false);
        cookie.setSecure(false);
        cookie.setMaxAge(60); // in seconds
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if(!ignoreRequest(httpRequest)) {
            Cookie cookie = WebUtils.getCookie(httpRequest, COOKIE_NAME);
            if(cookie != null){
                request.setAttribute(cookie.getValue(), true);
            }
        }

        // reset the flash message.
        setFlashMessage(httpResponse, "");

        chain.doFilter(request, response);
    }
    public boolean ignoreRequest(HttpServletRequest request) {
        RequestMatcher ignored = getIgnoredRequestMatchers();
        return ignored.matches(request);
    }


    public RequestMatcher getIgnoredRequestMatchers() {
        List<RequestMatcher> antPathRequestMatchers = new ArrayList<RequestMatcher>();
        antPathRequestMatchers.add(new AntPathRequestMatcher("/error"));
        antPathRequestMatchers.add(new AntPathRequestMatcher("/"));
        antPathRequestMatchers.add(new AntPathRequestMatcher("/css/**"));
        antPathRequestMatchers.add(new AntPathRequestMatcher("/js/**"));
        antPathRequestMatchers.add(new AntPathRequestMatcher("/assets/**"));
        antPathRequestMatchers.add(new AntPathRequestMatcher("/images/**"));
        antPathRequestMatchers.add(new AntPathRequestMatcher("/favicon.ico"));
        antPathRequestMatchers.add(new AntPathRequestMatcher("/styleguide/**"));
        return new OrRequestMatcher(antPathRequestMatchers);
    }
}
