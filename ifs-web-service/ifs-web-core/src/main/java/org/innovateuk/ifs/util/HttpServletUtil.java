package org.innovateuk.ifs.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HttpServletUtil {

    public HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
    }

    public HttpServletResponse response() {
        return ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
    }
}