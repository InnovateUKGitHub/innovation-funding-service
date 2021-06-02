package org.innovateuk.ifs.interceptors;

import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.user.resource.Role.*;

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
    public static final String SHOW_MANAGE_ASSESSORS_LINK_ATTR = "showManageAssessorsLink";

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Value("${logout.url}")
    private String logoutUrl;

    @Autowired
    private NavigationUtils navigationUtils;

    @Autowired
    private PageHistoryService pageHistoryService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && !(modelAndView.getView() instanceof RedirectView || modelAndView.getViewName().startsWith("redirect:"))) {
            UserResource user = userAuthenticationService.getAuthenticatedUser(request);
            addUserDashboardLink(request, modelAndView);
            addUserProfileLink(request, modelAndView);
            addLogoutLink(modelAndView, logoutUrl);
            addShowManageUsersAttribute(user, modelAndView);
            addShowManageAssessorsAttribute(user, modelAndView);
            Optional.of(handler)
                    .filter(HandlerMethod.class::isInstance)
                    .map(HandlerMethod.class::cast)
                    .ifPresent(handlerMethod -> pageHistoryService.recordPageHistory(request, response, modelAndView, handlerMethod));
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
        UserAuthentication authentication = (UserAuthentication) userAuthenticationService.getAuthentication(request);
        if (authentication != null) {
            UserResource user = authentication.getDetails();

            if (user.hasAnyRoles(ASSESSOR, KNOWLEDGE_TRANSFER_ADVISER)) {
                return Optional.of(ASSESSOR_PROFILE_URL);
            } else if (user.hasAnyRoles(APPLICANT, MONITORING_OFFICER)) {
                return Optional.of(USER_PROFILE_URL);
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private void addShowManageUsersAttribute(UserResource user, ModelAndView modelAndView) {
        modelAndView.getModelMap().addAttribute(SHOW_MANAGE_USERS_LINK_ATTR, user != null && user.hasAnyRoles(IFS_ADMINISTRATOR, SUPPORT, SUPER_ADMIN_USER));
    }

    private void addShowManageAssessorsAttribute(UserResource user, ModelAndView modelAndView) {
        modelAndView.getModelMap().addAttribute(SHOW_MANAGE_ASSESSORS_LINK_ATTR,
                        user != null &&
                        !user.hasAnyRoles(IFS_ADMINISTRATOR, SUPPORT, SUPER_ADMIN_USER) &&
                        user.hasAuthority(Authority.COMP_ADMIN));
    }

    public static void addLogoutLink(ModelAndView modelAndView, String logoutUrl) {
        modelAndView.addObject(USER_LOGOUT_LINK, logoutUrl);
    }
}