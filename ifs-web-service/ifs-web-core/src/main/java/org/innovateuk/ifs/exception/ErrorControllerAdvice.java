package org.innovateuk.ifs.exception;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.exception.*;
import org.innovateuk.ifs.interceptors.MenuLinksHandlerInterceptor;
import org.innovateuk.ifs.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.analytics.GoogleAnalyticsUtil.EMPTY_VALUE;
import static org.innovateuk.ifs.analytics.GoogleAnalyticsUtil.addGoogleAnalytics;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * This controller can handle all Exceptions, so the user should always gets a
 * nice looking error page, or a json error message is returned.
 * NOTE: Make sure every (non json) response uses createExceptionModelAndView as it also sets login and dashboard links
 */
@ControllerAdvice
public class ErrorControllerAdvice {
    private static final Log LOG = LogFactory.getLog(ErrorControllerAdvice.class);

    public static final String URL_HASH_INVALID_TEMPLATE = "url-hash-invalid";

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected Environment env;

    @Value("${logout.url}")
    private String logoutUrl;

    @Value("${ifs.web.googleanalytics.trackingid:" + EMPTY_VALUE + "}")
    private String googleAnalyticsKeys;

    public ErrorControllerAdvice() {
        super();
    }

    public ErrorControllerAdvice(Environment env, MessageSource messageSource) {
        super();
        this.env = env;
        this.messageSource = messageSource;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)     // 400
    @ExceptionHandler(value = {AutoSaveElementException.class})
    public @ResponseBody ObjectNode jsonAutosaveResponseHandler(AutoSaveElementException e) throws AutoSaveElementException {
        LOG.debug("ErrorController jsonAutosaveResponseHandler", e);
        return e.createJsonResponse();
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

    @ResponseStatus(value= HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ModelAndView methodArgumentMismatchHandler(HttpServletRequest req, MethodArgumentTypeMismatchException e) {
        LOG.debug("ErrorController  methodArgumentMismatchHandler", e);
        return createErrorModelAndViewWithStatusAndView(e, req, emptyList(), HttpStatus.NOT_FOUND, "404");
    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(value = IncorrectStateForPageException.class)
    public ModelAndView incorrectStateForPageHandler(HttpServletRequest req, IncorrectStateForPageException e) {
        LOG.debug("ErrorController  incorrectStateForPageHandler", e);
        return createErrorModelAndViewWithStatusAndView(e, req, emptyList(), HttpStatus.NOT_FOUND, "404");
    }

    @ResponseStatus(value= HttpStatus.METHOD_NOT_ALLOWED)  // 405
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ModelAndView httpRequestMethodNotSupportedHandler(HttpServletRequest req, HttpRequestMethodNotSupportedException e) {
        LOG.debug("ErrorController  httpRequestMethodNotSupportedHandler", e);
        return createErrorModelAndViewWithStatusAndView(e, req, emptyList(), HttpStatus.NOT_FOUND, "404");
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

    @ResponseStatus(value= HttpStatus.SERVICE_UNAVAILABLE)    //503
    @ExceptionHandler(value = {ServiceUnavailableException.class})
    public ModelAndView defaultErrorHandler(HttpServletRequest req, ServiceUnavailableException e) {
        LOG.debug("ErrorController  defaultErrorHandler", e);
        return createErrorModelAndViewWithStatusAndView(e, req, emptyList(), HttpStatus.SERVICE_UNAVAILABLE, "content/service-problems");
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

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(value = InviteExpiredException.class)
    public ModelAndView inviteClosedException(HttpServletRequest req, InviteExpiredException e) {
        LOG.debug("ErrorController inviteExpiredException", e);
        return createErrorModelAndViewWithTitleAndMessage(e, req, e.getArguments(), HttpStatus.BAD_REQUEST, "error.title.invite.expired", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(value = InviteAlreadySentException.class)
    public ModelAndView inviteClosedException(HttpServletRequest req, InviteAlreadySentException e) {
        LOG.debug("ErrorController inviteAlreadySentException", e);
        return createErrorModelAndViewWithTitleAndMessage(e, req, e.getArguments(), HttpStatus.BAD_REQUEST, "error.title.invite.already.sent", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(value = ApplicationAssessorAssignException.class)
    public ModelAndView applicationAssessorAssignException(HttpServletRequest req, ApplicationAssessorAssignException e) {
        return createErrorModelAndViewWithTitleAndMessage(e, req, e. getArguments(), HttpStatus.BAD_REQUEST, "error.title.application.assessor.assign", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN) // 403
    @ExceptionHandler(value = AssessmentWithdrawnException.class)
    public ModelAndView assessmentWithdrawnException(HttpServletRequest req, AssessmentWithdrawnException e) {
        return createErrorModelAndViewWithTitleAndMessage(e, req, e. getArguments(), HttpStatus.FORBIDDEN,
                "error.title.assessment.withdrawn", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = CompetitionFeedbackCantSendException.class)
    public ModelAndView competitionFeedbackCantSendException(HttpServletRequest req, CompetitionFeedbackCantSendException e) {
        return createErrorModelAndViewWithTitleAndMessage(e, req, e.getArguments(), HttpStatus.BAD_REQUEST,
                "error.title.competition.feedbackCantSend", e.getMessage());
    }


    protected ModelAndView createErrorModelAndViewWithStatus(Exception e, HttpServletRequest req, List<Object> arguments, HttpStatus status) {
        return createErrorModelAndViewWithStatusAndView(e, req, arguments, status, "error");
    }

    protected ModelAndView createErrorModelAndViewWithStatusAndView(Exception e, HttpServletRequest req, List<Object> arguments, HttpStatus status, String viewTemplate) {
        return createModelAndView(e, req, arguments, status, true, "error.title.status." + status.value(), null, viewTemplate);
    }

    protected ModelAndView createErrorModelAndViewWithTitleAndMessage(Exception e, HttpServletRequest req, List<Object> arguments, HttpStatus status, String titleKey, String messageKey) {
        return createModelAndView(e, req, arguments, status, false, titleKey, messageKey, "title-and-message-error");
    }

    protected ModelAndView createErrorModelAndViewWithUrlTitleAndMessage(Exception e, HttpServletRequest req, List<Object> arguments, HttpStatus status, String titleKey, String messageKey) {
        return createModelAndView(e, req, arguments, status, true, titleKey, messageKey, "title-and-message-error");
    }

    protected ModelAndView createErrorModelAndViewWithUrlTitleMessageAndView(Exception e, HttpServletRequest req, List<Object> arguments, HttpStatus status, String titleKey, String messageKey, String viewTemplate) {
        return createModelAndView(e, req, arguments, status, true, titleKey, messageKey, viewTemplate);
    }

    private ModelAndView createModelAndView(Exception e, HttpServletRequest req, List<Object> arguments, HttpStatus status, boolean showUrl, String titleKey, String messageKey, String viewTemplate) {
        String title = MessageUtil.getFromMessageBundle(messageSource, titleKey, "Unknown Error...", req.getLocale());

        String message;
        if (messageKey == null) {
            message = null;
        } else {
            message = MessageUtil.getFromMessageBundle(messageSource, messageKey, "Unknown Error...", null == arguments ? new Object[0] : arguments.toArray(), req.getLocale());
        }

        ModelAndView mav = new ModelAndView(viewTemplate, asMap("title", title, "messageForUser", message, "errorMessageClass", getErrorMessageClass(status)));
        // Needed here because postHandle of MenuLinkHandlerInterceptor may not be hit when there is an error.
        if (!(mav.getView() instanceof RedirectView || mav.getViewName().startsWith("redirect:"))) {
            addUserDashboardLink(mav);
            MenuLinksHandlerInterceptor.addLogoutLink(mav, logoutUrl);
        }
        if (showUrl) {
            mav.addObject("url", req.getRequestURL().toString());
        }
        addGoogleAnalytics(mav, googleAnalyticsKeys);
        mav.addAllObjects(populateExceptionMap(e, req, arguments));
        LOG.error("Error caught and returning error page.  Original error:", e);
        return mav;
    }

    private Map<String, Object> populateExceptionMap(Exception e, HttpServletRequest req, List<Object> arguments) {
        Map<String, Object> result = asMap("exception", e);
        if (env.acceptsProfiles("debug")) {
            result.put("stacktrace", ExceptionUtils.getStackTrace(e));
            result.put("message", MessageUtil.getFromMessageBundle(messageSource, e.getClass().getName(), e.getMessage(), arguments.toArray(), req.getLocale()));
        }
        return result;
    }

    private String getErrorMessageClass(HttpStatus status) {
        return status.name().toLowerCase();
    }

    /**
     * We cannot crate dashboard link here because user may not be logged in or have a role, so send them on main page
     *
     * @param modelAndView
     */
    private void addUserDashboardLink(ModelAndView modelAndView) {
        modelAndView.getModelMap().addAttribute(MenuLinksHandlerInterceptor.USER_DASHBOARD_LINK, "/");
    }

}
