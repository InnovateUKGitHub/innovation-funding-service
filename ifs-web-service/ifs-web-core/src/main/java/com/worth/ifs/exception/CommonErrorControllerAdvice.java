package com.worth.ifs.exception;

import com.worth.ifs.commons.error.exception.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static java.util.Collections.emptyList;

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
        return createErrorModelAndViewWithStatusAndView(e, req, e.getArguments(), HttpStatus.ALREADY_REPORTED, URL_HASH_INVALID_TEMPLATE);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)     // 400
    @ExceptionHandler(value = IncorrectArgumentTypeException.class)
    public ModelAndView incorrectArgumentTypeErrorHandler(HttpServletRequest req, IncorrectArgumentTypeException e) {
        LOG.debug("ErrorController incorrectArgumentTypeErrorHandler", e);
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(value = ForbiddenActionException.class)
    public ModelAndView forbiddenActionException(HttpServletRequest req, ForbiddenActionException e) {
        LOG.debug("ErrorController  actionNotAllowed", e);
        return createErrorModelAndViewWithStatusAndView(e, req, e.getArguments(), HttpStatus.FORBIDDEN, "forbidden");
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(value = AccessDeniedException.class)
    public ModelAndView accessDeniedException(HttpServletRequest req, AccessDeniedException e) {
        LOG.debug("ErrorController  actionNotAllowed", e);
        return createErrorModelAndViewWithStatusAndView(e, req, emptyList(), HttpStatus.FORBIDDEN, "forbidden");
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(value = ObjectNotFoundException.class)
    public ModelAndView objectNotFoundHandler(HttpServletRequest req, ObjectNotFoundException e) {
        LOG.debug("ErrorController  objectNotFoundHandler", e);
        return createErrorModelAndViewWithStatusAndView(e, req, e.getArguments(), HttpStatus.NOT_FOUND, "404");
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)    // 409
    @ExceptionHandler(value = FileAlreadyLinkedToFormInputResponseException.class)
    public ModelAndView fileAlreadyLinkedToFormInputResponse(HttpServletRequest req, FileAlreadyLinkedToFormInputResponseException e){
        LOG.debug("ErrorController fileAlreadyLinkedToFormInputResponse", e );
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.CONFLICT);
    }

    @ResponseStatus(value= HttpStatus.LENGTH_REQUIRED)  // 411
    @ExceptionHandler(value = LengthRequiredException.class)
    public ModelAndView lengthRequiredErrorHandler(HttpServletRequest req, LengthRequiredException e){
        LOG.debug("ErrorController lengthRequired", e );
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.LENGTH_REQUIRED);
    }

    @ResponseStatus(value= HttpStatus.PAYLOAD_TOO_LARGE)  // 413
    @ExceptionHandler(value = {MaxUploadSizeExceededException.class, MultipartException.class, PayloadTooLargeException.class})
    public ModelAndView payloadTooLargeErrorHandler(HttpServletRequest req, MultipartException e){
        LOG.debug("ErrorController payloadTooLarge", e );
        // TODO: Check if we can include more information by checking root cause as follows:
        // if(e.getRootCause() != null && e.getRootCause() instanceof FileUploadBase.FileSizeLimitExceededException)
        return createErrorModelAndViewWithStatusAndView(e, req, emptyList(), HttpStatus.PAYLOAD_TOO_LARGE, "413");
    }

    @ResponseStatus(value= HttpStatus.UNSUPPORTED_MEDIA_TYPE)  // 415
    @ExceptionHandler(value = UnsupportedMediaTypeException.class)
    public ModelAndView unsupportedMediaTypeErrorHandler(HttpServletRequest req, UnsupportedMediaTypeException e){
        LOG.debug("ErrorController unsupportedMediaType", e );
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToCreateFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToCreateFileException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToCreateFoldersException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToCreateFoldersException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToSendNotificationException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToSendNotificationException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToUpdateFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToUpdateFileException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToDeleteFileException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToDeleteFileException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = UnableToRenderNotificationTemplateException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, UnableToRenderNotificationTemplateException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createErrorModelAndViewWithStatus(e, req, e.getArguments(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)    //500
    @ExceptionHandler(value = {GeneralUnexpectedErrorException.class, Exception.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createErrorModelAndViewWithStatus(e, req, emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(value = FileAwaitingVirusScanException.class)
    public ModelAndView fileAwaitingScanning(HttpServletRequest req, FileAwaitingVirusScanException e) {
        LOG.debug("ErrorController  fileAwaitingScanning", e);
        return createErrorModelAndViewWithUrlTitleAndMessage(e, req, e.getArguments(), HttpStatus.FORBIDDEN, "error.title.file.awaiting.scanning", "error.message.file.awaiting.scanning");
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(value = FileQuarantinedException.class)
    public ModelAndView fileQuarantined(HttpServletRequest req, FileQuarantinedException e) {
        LOG.debug("ErrorController  fileQuarantined", e);
        return createErrorModelAndViewWithUrlTitleAndMessage(e, req, e.getArguments(), HttpStatus.FORBIDDEN, "error.title.file.quarantined", "error.message.file.quarantined");
    }

    @ResponseStatus(value= HttpStatus.FORBIDDEN) // 403
    @ExceptionHandler(value = RegistrationTokenExpiredException.class)
    public ModelAndView registrationTokenExpired(HttpServletRequest req, RegistrationTokenExpiredException e) {
        LOG.debug("ErrorController registrationTokenExpired", e);
        return createErrorModelAndViewWithUrlTitleMessageAndView(e, req, e.getArguments(), HttpStatus.FORBIDDEN, "error.title.registration.token.expired", "error.message.registration.token.expired", "registration-token-expired");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(value = UnableToAcceptInviteException.class)
    public ModelAndView unableToAcceptInviteException(HttpServletRequest req, UnableToAcceptInviteException e) {
        LOG.debug("ErrorController unableToAcceptInviteException", e);
        return createErrorModelAndViewWithTitleAndMessage(e, req, e.getArguments(), HttpStatus.BAD_REQUEST, "error.title.invite.accept.failure", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(value = InviteClosedException.class)
    public ModelAndView inviteClosedException(HttpServletRequest req, InviteClosedException e) {
        LOG.debug("ErrorController inviteClosedException", e);
        return createErrorModelAndViewWithTitleAndMessage(e, req, e.getArguments(), HttpStatus.BAD_REQUEST, "error.title.invite.closed", e.getMessage());
    }
}