package org.innovateuk.ifs.login.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.login.form.RoleSelectionForm;
import org.innovateuk.ifs.login.viewmodel.DashboardPanel;
import org.innovateuk.ifs.login.viewmodel.DashboardSelectionViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * This Controller redirects the request from http://<domain>/ to http://<domain>/login
 * So we don't have a public homepage, the login page is the homepage.
 */
@Controller
@SecuredBySpring(value = "Controller", description = "TODO", securedType = HomeController.class)
@PreAuthorize("permitAll")
public class HomeController {

    private static final Role[] ROLES_WITH_DASHBOARDS = { LIVE_PROJECTS_USER, APPLICANT, INNOVATION_LEAD, ASSESSOR, STAKEHOLDER };
    private static final List<Role> ROLES_WITH_DASHBOARDS_LIST = asList(ROLES_WITH_DASHBOARDS);

    @Autowired
    private NavigationUtils navigationUtils;

    @GetMapping("/")
    public String login() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (unauthenticated(authentication)) {
            return "redirect:/";
        }

        UserResource user = (UserResource) authentication.getDetails();

        if (user.hasMoreThanOneRoleOf(ROLES_WITH_DASHBOARDS)) {
            return "redirect:/roleSelection";
        }

        return getRedirectUrlForUser(user);
    }

    @GetMapping("/roleSelection")
    public String selectRole(HttpServletRequest request,
                             Model model,
                             @ModelAttribute(name = "form", binding = false) RoleSelectionForm form) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource user = (UserResource) authentication.getDetails();
        if (unauthenticated(authentication) || (!user.hasMoreThanOneRoleOf(ROLES_WITH_DASHBOARDS))){
            return "redirect:/";
        }

        return doViewDashboardSelection(request, model, user);
    }

    private String doViewDashboardSelection(HttpServletRequest request, Model model, UserResource user) {

        List<Role> dashboardRoles = simpleFilter(user.getRoles(), ROLES_WITH_DASHBOARDS_LIST::contains);
        List<DashboardPanel> dashboardPanels = simpleMap(dashboardRoles, role -> createDashboardPanelForRole(request, role));
        List<DashboardPanel> orderedPanels = sort(dashboardPanels,
                comparingInt(panel -> ROLES_WITH_DASHBOARDS_LIST.indexOf(panel.getRole())));

        model.addAttribute("model", new DashboardSelectionViewModel(orderedPanels));
        return "login/multiple-dashboard-choice";
    }

    private DashboardPanel createDashboardPanelForRole(HttpServletRequest request, Role role) {
        return new DashboardPanel(role, navigationUtils.getDirectDashboardUrlForRole(request, role));
    }

    private static boolean unauthenticated(Authentication authentication) {
        return authentication == null || !authentication.isAuthenticated() || authentication.getDetails() == null;
    }

    private String getRedirectUrlForUser(UserResource user) {

        if (user.getRoles().isEmpty()) {
            return "";
        }

        Role role = user.getRoles().get(0);
        return navigationUtils.getRedirectToDashboardUrlForRole(role);
    }
}