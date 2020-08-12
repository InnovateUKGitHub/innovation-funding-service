package org.innovateuk.ifs.login.controller;

import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.login.viewmodel.DashboardPanel;
import org.innovateuk.ifs.login.viewmodel.DashboardSelectionViewModel;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * This Controller redirects the request from http://<domain>/ to the relevant user dashboard based on the user's
 * global Role, or to a dashboard selection page for users who have more than one global Roles.
 */
@Controller
@SecuredBySpring(value = "Controller", description = "TODO", securedType = HomeController.class)
@PreAuthorize("permitAll")
public class HomeController {

    private NavigationUtils navigationUtils;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerRestService;

    HomeController(NavigationUtils navigationUtils) {
        this.navigationUtils = navigationUtils;
    }

    @GetMapping("/")
    @SecuredBySpring(value = "HomeController.defaultDashboardOrDashboardSelection()",
            description = "Authenticated users can gain access to the root URL")
    @PreAuthorize("isAuthenticated()")
    public String defaultDashboardOrDashboardSelection() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserResource user = (UserResource) authentication.getDetails();

        if (userShouldViewDashboardSelection(user)) {
            return "redirect:/dashboard-selection";
        }

        return getRedirectUrlForUser(user);
    }

    private boolean userShouldViewDashboardSelection(UserResource user) {
        return user.hasMoreThanOneRoleOf(multiDashboardRoles()) || user.hasRole(KNOWLEDGE_TRANSFER_ADVISOR);
    }

    @GetMapping("/dashboard-selection")
    @SecuredBySpring(value = "HomeController.selectDashboardForMultiRoleUsers()",
            description = "Authenticated users can gain access to the Dashboard selection page")
    @PreAuthorize("isAuthenticated()")
    public String selectDashboardForMultiRoleUsers(HttpServletRequest request,
                                                   Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource user = (UserResource) authentication.getDetails();
        if (!user.hasMoreThanOneRoleOf(multiDashboardRoles())) {
            return navigationUtils.getRedirectToLandingPageUrl(request);
        }

        return viewDashboardSelection(request, model, user);
    }

    private List<Role> createKtaDashboardRoles(UserResource user, List<Role> dashboardRoles) {
        List<ProcessRoleResource> processRoleResources = userRestService.findProcessRoleByUserId(user.getId()).getSuccess();
        boolean isMonitoringOfficer = monitoringOfficerRestService.isMonitoringOfficer(user.getId()).getSuccess();
        boolean isApplicant = processRoleResources.stream().map(ProcessRoleResource::getRole)
                .anyMatch(Role::isKta);
        boolean isAssessor = processRoleResources.stream().map(ProcessRoleResource::getRole)
                .anyMatch(Role::isAssessor);

        if (isMonitoringOfficer && !dashboardRoles.contains(MONITORING_OFFICER)) {
            dashboardRoles.add(MONITORING_OFFICER);
        }
        if (isApplicant && !dashboardRoles.contains(APPLICANT)) {
            dashboardRoles.add(APPLICANT);
        }
        if (isAssessor && !dashboardRoles.contains(ASSESSOR)) {
            dashboardRoles.add(ASSESSOR);
        }

        return dashboardRoles;
    }

    private String viewDashboardSelection(HttpServletRequest request, Model model, UserResource user) {
        List<Role> dashboardRoles = simpleFilter(user.getRoles(), multiDashboardRoles()::contains);

        if (user.hasRole(KNOWLEDGE_TRANSFER_ADVISOR)) {
            dashboardRoles = createKtaDashboardRoles(user, dashboardRoles);
        }

        if (dashboardRoles.size() == 1) {
            return navigationUtils.getRedirectToDashboardUrlForRole(dashboardRoles.get(0));
        }

        List<DashboardPanel> dashboardPanels = simpleMap(dashboardRoles, role -> createDashboardPanelForRole(request, role));
        List<DashboardPanel> orderedPanels = sort(dashboardPanels,
                comparingInt(panel -> asList(multiDashboardRoles()).indexOf(panel.getRole())));

        model.addAttribute("model", new DashboardSelectionViewModel(orderedPanels));
        return "login/multiple-dashboard-choice";
    }

    private DashboardPanel createDashboardPanelForRole(HttpServletRequest request, Role role) {
        return new DashboardPanel(role, navigationUtils.getDirectDashboardUrlForRole(request, role));
    }

    private String getRedirectUrlForUser(UserResource user) {

        if (user.getRoles().isEmpty()) {
            throw new ForbiddenActionException("Unable to access dashboards without at least one role");
        }

        Role role = user.getRoles().get(0);
        return navigationUtils.getRedirectToDashboardUrlForRole(role);
    }
}