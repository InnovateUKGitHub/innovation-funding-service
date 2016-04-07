package com.worth.ifs.exception;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

import com.worth.ifs.commons.error.exception.FileAlreadyLinkedToFormInputResponseException;
import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.commons.error.exception.GeneralUnexpectedErrorException;
import com.worth.ifs.commons.error.exception.IncorrectArgumentTypeException;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.error.exception.LengthRequiredException;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.error.exception.PayloadTooLargeException;
import com.worth.ifs.commons.error.exception.UnableToCreateFileException;
import com.worth.ifs.commons.error.exception.UnableToCreateFoldersException;
import com.worth.ifs.commons.error.exception.UnableToDeleteFileException;
import com.worth.ifs.commons.error.exception.UnableToRenderNotificationTemplateException;
import com.worth.ifs.commons.error.exception.UnableToSendNotificationException;
import com.worth.ifs.commons.error.exception.UnableToUpdateFileException;
import com.worth.ifs.commons.error.exception.UnsupportedMediaTypeException;

/**
 * This controller can handle all Exceptions, so the user should always gets a
 * nice looking error page, or a json error message is returned.
 * NOTE: Make sure every (non json) response uses createExceptionModelAndView as it also sets login and dashboard links
 */
public abstract class CommonErrorControllerAdvice extends BaseErrorControllerAdvice {
    private static final Log LOG = LogFactory.getLog(CommonErrorControllerAdvice.class);
    public static final String URL_HASH_INVALID_TEMPLATE = "url-hash-invalid";

    public CommonErrorControllerAdvice() {
        super();
    }

    public CommonErrorControllerAdvice(Environment env, MessageSource messageSource) {
        super();
        this.env = env;
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.ALREADY_REPORTED)     // 208
    @ExceptionHandler(value = InvalidURLException.class)
    public ModelAndView invalidUrlErrorHandler(HttpServletRequest req, InvalidURLException e) {
        LOG.debug("ErrorController invalidUrlErrorHandler", e);
        return createExceptionModelAndView(e, "url-hash-invalid", req, e.getArguments(), HttpStatus.ALREADY_REPORTED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)     // 400
    @ExceptionHandler(value = IncorrectArgumentTypeException.class)
    public ModelAndView incorrectArgumentTypeErrorHandler(HttpServletRequest req, IncorrectArgumentTypeException e) {
        LOG.debug("ErrorController incorrectArgumentTypeErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(value = ForbiddenActionException.class)
    public ModelAndView accessDeniedException(HttpServletRequest req, ForbiddenActionException e) {
        LOG.debug("ErrorController  actionNotAllowed", e);
        return createExceptionModelAndView(e, "forbidden", req, e.getArguments(), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(value = ObjectNotFoundException.class)
    public ModelAndView objectNotFoundHandler(HttpServletRequest req, ObjectNotFoundException e) {
        LOG.debug("ErrorController  objectNotFoundHandler", e);
        return createExceptionModelAndView(e, "404", req, e.getArguments(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)    // 409
    @ExceptionHandler(value = FileAlreadyLinkedToFormInputResponseException.class)
    public ModelAndView fileAlreadyLinkedToFormInputResponse(HttpServletRequest req, FileAlreadyLinkedToFormInputResponseException e){
        LOG.debug("ErrorController fileAlreadyLinkedToFormInputResponse", e );
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(value= HttpStatus.LENGTH_REQUIRED)  // 411
    @ExceptionHandler(value = LengthRequiredException.class)
    public ModelAndView lengthRequiredErrorHandler(HttpServletRequest req, LengthRequiredException e){
        LOG.debug("ErrorController lengthRequired", e );
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.LENGTH_REQUIRED);
    }

    @ResponseStatus(value= HttpStatus.PAYLOAD_TOO_LARGE)  // 413
    @ExceptionHandler(value = {MaxUploadSizeExceededException.class, MultipartException.class, PayloadTooLargeException.class})
    public ModelAndView payloadTooLargeErrorHandler(HttpServletRequest req, MultipartException e){
        LOG.debug("ErrorController payloadTooLarge", e );
        // TODO: Check if we can include more information by checking root cause as follows:
        // if(e.getRootCause() != null && e.getRootCause() instanceof FileUploadBase.FileSizeLimitExceededException)
        return createExceptionModelAndView(e, "413", req, Collections.emptyList(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ResponseStatus(value= HttpStatus.UNSUPPORTED_MEDIA_TYPE)  // 415
    @ExceptionHandler(value = UnsupportedMediaTypeException.class)
    public ModelAndView unsupportedMediaTypeErrorHandler(HttpServletRequest req, UnsupportedMediaTypeException e){
        LOG.debug("ErrorController unsupportedMediaType", e );
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToCreateFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToCreateFileException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToCreateFoldersException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToCreateFoldersException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToSendNotificationException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToSendNotificationException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToUpdateFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToUpdateFileException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToDeleteFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToDeleteFileException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToRenderNotificationTemplateException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToRenderNotificationTemplateException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = {GeneralUnexpectedErrorException.class, Exception.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createExceptionModelAndView(e, "error", req, Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}