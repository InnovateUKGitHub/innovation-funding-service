package org.innovateuk.ifs.interceptors;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;

/**
 * Have the menu links globally available for each controller.
 * So it does not have to be added to each call separately anymore.
 */
public class MenuLinksHandlerInterceptor extends HandlerInterceptorAdapter {

    public static final String USER_DASHBOARD_LINK = "userDashboardLink";
    public static final String USER_LOGOUT_LINK = "logoutUrl";
    public static final String USER_PROFILE_LINK = "userProfileLink";
    public static final String ASSESSOR_PROFILE_URL = "/assessment/profile/details";
    public static final String USER_PROFILE_URL = "/profile/view";
    public static final String SHOW_MANAGE_USERS_LINK_ATTR = "showManageUsersLink";

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Value("${logout.url}")
    private String logoutUrl;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private NavigationUtils navigationUtils;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && !(modelAndView.getView() instanceof RedirectView || modelAndView.getViewName().startsWith("redirect:"))) {
            addUserDashboardLink(request, modelAndView);
            addUserProfileLink(request, modelAndView);
            addLogoutLink(modelAndView, logoutUrl);
            addShowManageUsersAttribute(request, modelAndView);
        }
    }

    private void addUserDashboardLink(HttpServletRequest request, ModelAndView modelAndView) {
        String dashboardUrl = navigationUtils.getDirectLandingPageUrl(request);
        modelAndView.getModelMap().addAttribute(USER_DASHBOARD_LINK, dashboardUrl);
    }

    private void addUserProfileLink(HttpServletRequest request, ModelAndView modelAndView) {
        Optional<String> profileUrl = getUserProfileUrl(request);
        profileUrl.ifPresent(url -> modelAndView.getModelMap().addAttribute(USER_PROFILE_LINK, url));
    }

    private Optional<String> getUserProfileUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();

        switch (contextPath) {
            case "/assessment": return Optional.of(ASSESSOR_PROFILE_URL);
            case "": return Optional.of(USER_PROFILE_URL);
            case "/project-setup": return Optional.of(USER_PROFILE_URL);
            default: return Optional.empty();
        }
    }
    
    private void addShowManageUsersAttribute(HttpServletRequest request, ModelAndView modelAndView) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        modelAndView.getModelMap().addAttribute(SHOW_MANAGE_USERS_LINK_ATTR, user != null && user.hasRole(IFS_ADMINISTRATOR));
    }

    public static void addLogoutLink(ModelAndView modelAndView, String logoutUrl) {
        modelAndView.addObject(USER_LOGOUT_LINK, logoutUrl);
    }
}
