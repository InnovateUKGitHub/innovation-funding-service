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

public abstract class BaseErrorControllerAdvice {

    private static final Log LOG = LogFactory.getLog(BaseErrorControllerAdvice.class);

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected Environment env;

    @Value("${logout.url}")
    private String logoutUrl;

    protected ModelAndView createExceptionModelAndView(Exception e, String pageName, HttpServletRequest req, List<Object> arguments, HttpStatus status){
        return createExceptionModelAndView(e, "error.title.status." + status.value(), pageName, req, arguments, status);
    }

    protected ModelAndView createExceptionModelAndView(Exception e, String title, String pageName, HttpServletRequest req, List<Object> arguments, HttpStatus status){
        return createExceptionModelAndView(e, title, null, pageName, req, arguments, status);
    }

    protected ModelAndView createExceptionModelAndViewWithTitleAndMessage(Exception e, String titleKey, String messageForUserKey, HttpServletRequest req, List<Object> arguments, HttpStatus status) {
        return createExceptionModelAndView(e, titleKey, messageForUserKey, "title-and-message-error", req, arguments, status);
    }

    protected ModelAndView createExceptionModelAndView(Exception e, String titleKey, String messageForUserKey, String pageName, HttpServletRequest req, List<Object> arguments, HttpStatus status){

        Object[] argumentsArray = arguments == null ? new Object[0] : arguments.toArray();
        String errorTitle = MessageUtil.getFromMessageBundle(messageSource, titleKey, "Unknown Error...", req.getLocale());
        String messageForUser = MessageUtil.getFromMessageBundle(messageSource, messageForUserKey, "Unknown Error...", argumentsArray, req.getLocale());

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("title", errorTitle);
        mav.addObject("messageForUser", messageForUser);
        mav.addObject("errorMessageClass", status.name().toLowerCase());

        if(env.acceptsProfiles("debug")) {
            mav.addObject("stacktrace", ExceptionUtils.getStackTrace(e));
            String msg = MessageUtil.getFromMessageBundle(messageSource, e.getClass().getName(), e.getMessage(), arguments.toArray(), req.getLocale());
            mav.addObject("message", msg);
        }

        mav.addObject("url", req.getRequestURL().toString());
        mav.setViewName(pageName);

        // Needed here because postHandle of MenuLinkHandlerInterceptior may not be hit when there is an error.
        if(!(mav.getView() instanceof RedirectView || mav.getViewName().startsWith("redirect:") )) {
            addUserDashboardLink(mav);
            MenuLinksHandlerInterceptor.addLogoutLink(mav, logoutUrl);
        }

        LOG.error("Error caught and returning error page.  Original error:", e);

        return mav;
    }

    /**
     *  We cannot crate dashboard link here because user may not be logged in or have a role, so send them on main page
     * @param modelAndView
     */
    public static void addUserDashboardLink(ModelAndView modelAndView) {
        modelAndView.getModelMap().addAttribute(MenuLinksHandlerInterceptor.USER_DASHBOARD_LINK, "/");
    }
}
