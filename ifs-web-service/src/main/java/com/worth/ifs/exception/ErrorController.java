package com.worth.ifs.exception;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.commons.error.exception.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller can handle all Exceptions, so the user should always gets a
 * nice looking error page, or a json error message is returned.
 */
@ControllerAdvice
public class ErrorController {
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private MessageSource messageSource;

    @Autowired
    Environment env;

    public ErrorController() {
        super();
    }

    public ErrorController(Environment env, MessageSource messageSource) {
        super();
        this.env = env;
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)     // 400
    @ExceptionHandler(value = {AutosaveElementException.class, IncorrectArgumentTypeException.class})
    public @ResponseBody ObjectNode jsonAutosaveResponseHandler(AutosaveElementException e) throws AutosaveElementException {
        log.debug("ErrorController jsonAutosaveResponseHandler", e);
        return e.createJsonResponse();
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(value = ObjectNotFoundException.class)
    public ModelAndView objectNotFoundHandler(HttpServletRequest req, Exception e) {
        log.debug("ErrorController  objectNotFoundHandler", e);
        return createExceptionModelAndView(e, "404", req);
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(value = AccessDeniedException.class)
    public ModelAndView accessDeniedException(HttpServletRequest req, Exception e) {
        log.debug("ErrorController  actionNotAllowed", e);
        return createExceptionModelAndView(e, "forbidden", req);
    }

    @ResponseStatus(value= HttpStatus.UNSUPPORTED_MEDIA_TYPE)  // 415
    @ExceptionHandler(value = UnsupportedMediaTypeException.class)
    public ModelAndView unsupportedMediaTypeErrorHandler(HttpServletRequest req, Exception e){
        log.debug("ErrorController unsupportedMediaType", e );
        return createExceptionModelAndView(e, "error", req);
    }

    @ResponseStatus(value= HttpStatus.PAYLOAD_TOO_LARGE)  // 413
    @ExceptionHandler(value = PayloadTooLargeException.class)
    public ModelAndView payloadTooLargeErrorHandler(HttpServletRequest req, Exception e){
        log.debug("ErrorController payloadTooLarge", e );
        return createExceptionModelAndView(e, "error", req);
    }

    @ResponseStatus(value= HttpStatus.LENGTH_REQUIRED)  // 411
    @ExceptionHandler(value = LengthRequiredException.class)
    public ModelAndView lengthRequiredErrorHandler(HttpServletRequest req, Exception e){
        log.debug("ErrorController lengthRequired", e );
        return createExceptionModelAndView(e, "error", req);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)    // 409
    @ExceptionHandler(value = FileAlreadyLinkedToFormInputResponseException.class)
    public ModelAndView fileAlreadyLinkedToFormInputResponse(HttpServletRequest req, Exception e){
        log.debug("ErrorController fileAlreadyLinkedToFormInputResponse", e );
        return createExceptionModelAndView(e, "error", req);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = {GeneralUnexpectedErrorException.class, UnableToCreateFileException.class,
            UnableToCreateFoldersException.class, UnableToSendNotificationException.class,
            UnableToUpdateFileException.class, UnableToDeleteFileException.class,
            UnableToRenderNotificationTemplateException.class, Exception.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        log.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req);
    }

    private ModelAndView createExceptionModelAndView(Exception e, String message, HttpServletRequest req){
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);

        if(env.acceptsProfiles("uat", "dev", "test")) {
            mav.addObject("stacktrace", ExceptionUtils.getStackTrace(e));
        }
        String msg = messageSource.getMessage(e.getClass().getName(), null, req.getLocale());
        if(msg != null){
            msg = e.getMessage();
        }
        mav.addObject("message", msg);
        mav.addObject("url", req.getRequestURL().toString());
        mav.setViewName(message);
        return mav;
    }
}