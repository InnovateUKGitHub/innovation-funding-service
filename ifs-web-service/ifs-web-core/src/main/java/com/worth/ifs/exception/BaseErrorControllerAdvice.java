package com.worth.ifs.exception;

import com.worth.ifs.interceptors.MenuLinksHandlerInterceptor;
import com.worth.ifs.util.MessageUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.MapFunctions.asMap;

public abstract class BaseErrorControllerAdvice {

    private static final Log LOG = LogFactory.getLog(BaseErrorControllerAdvice.class);

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected Environment env;

    @Value("${logout.url}")
    private String logoutUrl;

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
        String message = MessageUtil.getFromMessageBundle(messageSource, messageKey, "Unknown Error...", null == arguments ? new Object[0] : arguments.toArray(), req.getLocale());
        ModelAndView mav = new ModelAndView(viewTemplate, asMap("title", title, "messageForUser", message, "errorMessageClass", getErrorMessageClass(status)));
        // Needed here because postHandle of MenuLinkHandlerInterceptior may not be hit when there is an error.
        if (!(mav.getView() instanceof RedirectView || mav.getViewName().startsWith("redirect:"))) {
            addUserDashboardLink(mav);
            MenuLinksHandlerInterceptor.addLogoutLink(mav, logoutUrl);
        }
        if (showUrl) {
            mav.addObject("url", req.getRequestURL().toString());
        }
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
