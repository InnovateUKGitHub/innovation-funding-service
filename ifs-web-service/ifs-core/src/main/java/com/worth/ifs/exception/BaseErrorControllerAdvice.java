package com.worth.ifs.exception;

import com.worth.ifs.commons.security.UserAuthenticationService;
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

    @Autowired
    private UserAuthenticationService userAuthenticationService;

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
