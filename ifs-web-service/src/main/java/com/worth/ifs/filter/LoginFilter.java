package com.worth.ifs.filter;

import com.worth.ifs.domain.User;
import com.worth.ifs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Configuration
@Component("LoginFilter")
public class LoginFilter extends OncePerRequestFilter {
    public static final String IFS_AUTH_COOKIE_NAME = "IFS_authToken";
    @Autowired
    private UserService userService;

    /**
     * Redirect the request to the login page when the user is has no auth token.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        Cookie authCookie = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(IFS_AUTH_COOKIE_NAME)) {
                authCookie = cookie;
            }
        }

        // when the current request should only be accessable by a authenticated user,
        // get the authCookie and check if the user exists.

        if (!request.getRequestURI().startsWith("/login") &&
                !request.getRequestURI().startsWith("/logout") &&
                !request.getRequestURI().startsWith("/css") &&
                !request.getRequestURI().startsWith("/js") &&
                !request.getRequestURI().startsWith("/images") &&
                !request.getRequestURI().startsWith("/assets")
                ) {
            if (authCookie != null && authCookie.getValue() != null && authCookie.getValue() != "") {
                User user = userService.retrieveUserByToken(authCookie.getValue());
                if (user == null) {
                    response.sendRedirect("/login");
                }
            } else {
                response.sendRedirect("/login");
                return;
            }
        }


        chain.doFilter(request, response);
    }

}
