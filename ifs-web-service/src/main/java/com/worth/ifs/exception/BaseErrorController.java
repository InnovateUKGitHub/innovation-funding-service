package com.worth.ifs.exception;

import com.worth.ifs.util.MessageUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class BaseErrorController {
    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected Environment env;

    protected ModelAndView createExceptionModelAndView(Exception e, String message, HttpServletRequest req, List<Object> arguments, HttpStatus status){
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        final String errorPageTitle = MessageUtil.getFromMessageBundle(messageSource, "error.title.status." + status.value(), "Unknown Error...", arguments.toArray(), req.getLocale());
        mav.addObject("title", errorPageTitle);
        if(env.acceptsProfiles("uat", "dev", "test")) {
            mav.addObject("stacktrace", ExceptionUtils.getStackTrace(e));
            String msg = MessageUtil.getFromMessageBundle(messageSource, e.getClass().getName(), e.getMessage(), arguments.toArray(), req.getLocale());
            mav.addObject("message", msg);
        }

        mav.addObject("url", req.getRequestURL().toString());
        mav.setViewName(message);
        return mav;
    }
}
