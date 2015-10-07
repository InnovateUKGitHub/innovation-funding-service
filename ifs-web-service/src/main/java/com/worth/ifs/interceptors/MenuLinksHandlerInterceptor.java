package com.worth.ifs.interceptors;

import com.worth.ifs.security.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    public static final String DASHBOARD_LINK="/dashboard";

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if(modelAndView!=null && !(modelAndView.getView() instanceof RedirectView || modelAndView.getViewName().startsWith("redirect:") )) {
            addUserDashboardLink(request, modelAndView);
        }
    }

    private void addUserDashboardLink(HttpServletRequest request, ModelAndView modelAndView) {
        String roleName = getRoleName(request);
        if(!roleName.isEmpty()) {
            modelAndView.getModelMap().addAttribute(USER_DASHBOARD_LINK, "/" + roleName + DASHBOARD_LINK);
        }
    }

    private String  getRoleName(HttpServletRequest request) {
        Authentication authentication = userAuthenticationService.getAuthentication(request);
        if(authentication!=null) {
            Optional<SimpleGrantedAuthority> simpleGrantedAuthority = (Optional<SimpleGrantedAuthority>)authentication.getAuthorities().stream().findFirst();
            if(simpleGrantedAuthority.isPresent()) {
                return simpleGrantedAuthority.get().getAuthority();
            }
        }
        return "";
    }
}
