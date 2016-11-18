package com.worth.ifs.interceptors;

import com.worth.ifs.commons.security.authentication.user.UserAuthentication;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Have the menu links globally available for each controller.
 * So it does not have to be added to each call separately anymore.
 */
public class MenuLinksHandlerInterceptor extends HandlerInterceptorAdapter {

    public static final String USER_DASHBOARD_LINK="userDashboardLink";

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Value("${logout.url}")
    private String logoutUrl;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if(modelAndView!=null && !(modelAndView.getView() instanceof RedirectView || modelAndView.getViewName().startsWith("redirect:") )) {
            addUserDashboardLink(request, modelAndView);
            addLogoutLink(modelAndView, logoutUrl);
        }
    }

    private void addUserDashboardLink(HttpServletRequest request, ModelAndView modelAndView) {
        String dashboardUrl = getDashboardUrl(request);
        if(!dashboardUrl.isEmpty()) {
            modelAndView.getModelMap().addAttribute(USER_DASHBOARD_LINK, dashboardUrl);
        }
    }

    public static void addLogoutLink(ModelAndView modelAndView, String logoutUrl) {
        modelAndView.addObject("logoutUrl", logoutUrl);
    }

    /**
     * Get the dashboard url, from the Role object.
     */
    private String  getDashboardUrl(HttpServletRequest request) {
        UserAuthentication authentication = (UserAuthentication) userAuthenticationService.getAuthentication(request);
        if(authentication!=null) {
            Optional<SimpleGrantedAuthority> simpleGrantedAuthority = (Optional<SimpleGrantedAuthority>)authentication.getAuthorities().stream().findFirst();
            if(simpleGrantedAuthority.isPresent()) {
                UserResource details = authentication.getDetails();
                return "/" + details.getRoles().get(0).getUrl();
            }
        }
        return "/";
    }
}
