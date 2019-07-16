package org.innovateuk.ifs.interceptors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.navigation.NavigationRoot;
import org.innovateuk.ifs.navigation.PageHistory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.EncodedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Deque;
import java.util.LinkedList;
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
    private NavigationUtils navigationUtils;

    @Autowired
    private EncodedCookieService encodedCookieService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && !(modelAndView.getView() instanceof RedirectView || modelAndView.getViewName().startsWith("redirect:"))) {
            addUserDashboardLink(request, modelAndView);
            addUserProfileLink(request, modelAndView);
            addLogoutLink(modelAndView, logoutUrl);
            addShowManageUsersAttribute(request, modelAndView);
            handleBackLink(request, response, modelAndView, handler);
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
            case "/assessment":
                return Optional.of(ASSESSOR_PROFILE_URL);
            case "":
                return Optional.of(USER_PROFILE_URL);
            case "/project-setup":
                return Optional.of(USER_PROFILE_URL);
            default:
                return Optional.empty();
        }
    }

    private void addShowManageUsersAttribute(HttpServletRequest request, ModelAndView modelAndView) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        modelAndView.getModelMap().addAttribute(SHOW_MANAGE_USERS_LINK_ATTR, user != null && user.hasRole(IFS_ADMINISTRATOR));
    }

    public static void addLogoutLink(ModelAndView modelAndView, String logoutUrl) {
        modelAndView.addObject(USER_LOGOUT_LINK, logoutUrl);
    }

    private void handleBackLink(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView, Object handler) {
        Optional<Deque<PageHistory>> cookie = encodedCookieService.getCookieAs(request, "pageHistory", new TypeReference<Deque<PageHistory>>() {});
        Deque<PageHistory> history = cookie.orElse(new LinkedList<>());
        while (history.contains(new PageHistory(request.getRequestURI()))) {
            history.pop();
        }

        if (!history.isEmpty()) {
            modelAndView.getModel().put("cookieBackLinkUrl", history.peek().getUrl());
            modelAndView.getModel().put("cookieBackLinkText", history.peek().getName());
        }

        boolean navigationRoot = Optional.of(handler)
                .filter(HandlerMethod.class::isInstance)
                .map(HandlerMethod.class::cast)
                .filter(method -> method.hasMethodAnnotation(NavigationRoot.class))
                .isPresent();

        if (navigationRoot) {
            history.clear();
        }

        history.push(new PageHistory(request.getRequestURI()));
        encodedCookieService.saveToCookie(response, "pageHistory", JsonUtil.getSerializedObject(history));
    }
}
