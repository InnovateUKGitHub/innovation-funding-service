package com.worth.ifs.exception;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorController {
    private final Log log = LogFactory.getLog(getClass());

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = AutosaveElementException.class)
    public @ResponseBody ObjectNode jsonAutosaveResponseHandler(HttpServletRequest req, AutosaveElementException e) throws Exception {

        log.debug("ErrorController jsonAutosaveResponseHandler", e);
        return e.createJsonResponse();
    }

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {

        log.debug("ErrorController  defaultErrorHandler", e);

        // Otherwise setup and send the user to a default error-view.
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        return mav;
    }

    @ExceptionHandler(value = ObjectNotFoundException.class)
    public ModelAndView objectNotFoundHandler(HttpServletRequest req, Exception e) throws Exception {
        log.debug("ErrorController  objectNotFoundHandler", e);

        // Otherwise setup and send the user to a default error-view.
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("404");
        return mav;
    }
}