package com.worth.ifs.assessment.controller;

import com.worth.ifs.exception.CommonErrorControllerAdvice;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * This controller can handle all Exceptions, so the user should always gets a
 * nice looking error page, or a json error message is returned.
 * NOTE: Make sure every (non json) response uses createExceptionModelAndView as it also sets login and dashboard links
 */
@ControllerAdvice
public class ErrorControllerAdvice extends CommonErrorControllerAdvice {
    public ErrorControllerAdvice() {
        super();
    }

    public ErrorControllerAdvice(Environment env, MessageSource messageSource) {
        super();
        this.env = env;
        this.messageSource = messageSource;
    }

}