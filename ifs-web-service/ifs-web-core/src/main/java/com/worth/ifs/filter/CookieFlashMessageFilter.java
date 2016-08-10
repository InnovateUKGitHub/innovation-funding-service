package com.worth.ifs.filter;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;
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
    public static final String COOKIE_NAME = "flashMessage";

    @Value("${server.session.cookie.secure}")
    private boolean cookieSecure;

    @Value("${server.session.cookie.http-only}")
    private boolean cookieHttpOnly;
    /**
     * Cookie is set, just for after redirecting, so the lifetime should be short.
     */
    public void setFlashMessage(HttpServletResponse response, String name){
        Cookie cookie = new Cookie(COOKIE_NAME, name);
        cookie.setMaxAge(60); // in seconds
        cookie.setSecure(cookieSecure);
        cookie.setHttpOnly(cookieHttpOnly);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void removeFlashMessage(HttpServletResponse response){
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0); // in seconds
        cookie.setSecure(cookieSecure);
        cookie.setHttpOnly(cookieHttpOnly);
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
        removeFlashMessage(httpResponse);

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
        return new OrRequestMatcher(antPathRequestMatchers);
    }
}
