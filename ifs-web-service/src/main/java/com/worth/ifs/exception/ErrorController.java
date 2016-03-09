package com.worth.ifs.exception;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.commons.error.exception.*;
import com.worth.ifs.util.MessageUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

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
    @ExceptionHandler(value = {AutosaveElementException.class})
    public @ResponseBody ObjectNode jsonAutosaveResponseHandler(AutosaveElementException e) throws AutosaveElementException {
        log.debug("ErrorController jsonAutosaveResponseHandler", e);
        return e.createJsonResponse();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)     // 400
    @ExceptionHandler(value = IncorrectArgumentTypeException.class)
    public ModelAndView incorrectArgumentTypeErrorHandler(HttpServletRequest req, IncorrectArgumentTypeException e) {
        log.debug("ErrorController incorrectArgumentTypeErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(value = ForbiddenActionException.class)
    public ModelAndView accessDeniedException(HttpServletRequest req, ForbiddenActionException e) {
        log.debug("ErrorController  actionNotAllowed", e);
        return createExceptionModelAndView(e, "forbidden", req, e.getArguments(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(value = ObjectNotFoundException.class)
    public ModelAndView objectNotFoundHandler(HttpServletRequest req, ObjectNotFoundException e) {
        log.debug("ErrorController  objectNotFoundHandler", e);
        return createExceptionModelAndView(e, "404", req, e.getArguments(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)    // 409
    @ExceptionHandler(value = FileAlreadyLinkedToFormInputResponseException.class)
    public ModelAndView fileAlreadyLinkedToFormInputResponse(HttpServletRequest req, FileAlreadyLinkedToFormInputResponseException e){
        log.debug("ErrorController fileAlreadyLinkedToFormInputResponse", e );
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(value= HttpStatus.LENGTH_REQUIRED)  // 411
    @ExceptionHandler(value = LengthRequiredException.class)
    public ModelAndView lengthRequiredErrorHandler(HttpServletRequest req, LengthRequiredException e){
        log.debug("ErrorController lengthRequired", e );
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.LENGTH_REQUIRED);
    }

    @ResponseStatus(value= HttpStatus.PAYLOAD_TOO_LARGE)  // 413
    @ExceptionHandler(value = PayloadTooLargeException.class)
    public ModelAndView payloadTooLargeErrorHandler(HttpServletRequest req, PayloadTooLargeException e){
        log.debug("ErrorController payloadTooLarge", e );
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ResponseStatus(value= HttpStatus.UNSUPPORTED_MEDIA_TYPE)  // 415
    @ExceptionHandler(value = UnsupportedMediaTypeException.class)
    public ModelAndView unsupportedMediaTypeErrorHandler(HttpServletRequest req, UnsupportedMediaTypeException e){
        log.debug("ErrorController unsupportedMediaType", e );
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToCreateFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToCreateFileException e) {
        log.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToCreateFoldersException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToCreateFoldersException e) {
        log.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToSendNotificationException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToSendNotificationException e) {
        log.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToUpdateFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToUpdateFileException e) {
        log.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToDeleteFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToDeleteFileException e) {
        log.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToRenderNotificationTemplateException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToRenderNotificationTemplateException e) {
        log.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = {GeneralUnexpectedErrorException.class, Exception.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        log.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView createExceptionModelAndView(Exception e, String message, HttpServletRequest req, List<Object> arguments, HttpStatus status){
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