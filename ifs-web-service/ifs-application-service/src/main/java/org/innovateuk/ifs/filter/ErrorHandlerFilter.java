package org.innovateuk.ifs.filter;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            if(ex instanceof NestedServletException && ((NestedServletException) ex).getRootCause() instanceof org.thymeleaf.exceptions.TemplateProcessingException) {
                    request.setAttribute("message", "There was a problem processing this request.  Please contact site administrator");
                    RequestDispatcher rd = request.getRequestDispatcher("/rendering-error");
                    rd.forward(request, response);
            }
        }
    }
}
