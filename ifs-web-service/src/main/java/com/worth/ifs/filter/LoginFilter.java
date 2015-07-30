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
            if(cookie.getName().equals(IFS_AUTH_COOKIE_NAME)){
                authCookie = cookie;
            }
        }

        if(!request.getRequestURI().equals("/login") &&
                !request.getRequestURI().equals("/logout") &&
                !request.getRequestURI().startsWith("/css") &&
                !request.getRequestURI().startsWith("/js") &&
                !request.getRequestURI().startsWith("/images") &&
                !request.getRequestURI().startsWith("/assets")
                ){
            if(authCookie != null){
                if(authCookie.getValue() != null && authCookie.getValue() != ""){
                    User user = userService.retrieveUserByToken(authCookie.getValue());
                    if(user == null){
                        System.out.println("Redirect to login");
                        response.sendRedirect("/login");
                    }else{
                        System.out.println("No redirect to login, user is authenticated");
                    }
                }else{
                    System.out.println("Redirect to login1");
                    response.sendRedirect("/login");
                }
            } else {
                System.out.println("Redirect to login2");
                response.sendRedirect("/login");
                return;
            }
        }



        chain.doFilter(request, response);
    }

}
