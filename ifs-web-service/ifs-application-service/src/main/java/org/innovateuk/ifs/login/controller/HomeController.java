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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.sort;

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
        Set<Role> roles = getMultiDashboardRoles(user);

        if (roles.size() > 1) {
            return "redirect:/dashboard-selection";
        }

        return getRedirectUrlForUser(user);
    }

    @GetMapping("/dashboard-selection")
    @SecuredBySpring(value = "HomeController.selectDashboardForMultiRoleUsers()",
            description = "Authenticated users can gain access to the Dashboard selection page")
    @PreAuthorize("isAuthenticated()")
    public String selectDashboardForMultiRoleUsers(HttpServletRequest request,
                                                   Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserResource user = (UserResource) authentication.getDetails();
        Set<Role> roles = getMultiDashboardRoles(user);

        if (roles.size() < 1) {
            return navigationUtils.getRedirectToLandingPageUrl(request);
        }

        return viewDashboardSelection(request, model, roles);
    }

    private Set<Role> getMultiDashboardRoles(UserResource user) {
        Set<Role> dashboardRoles = user.getRoles().stream().filter(multiDashboardRoles()::contains).collect(Collectors.toSet());

        if (user.hasRole(KNOWLEDGE_TRANSFER_ADVISER)) {
            addKtaRoles(user, dashboardRoles);
        }
        return dashboardRoles;
    }

    private void addKtaRoles(UserResource user, Set<Role> dashboardRoles) {
        List<ProcessRoleResource> processRoleResources = userRestService.findProcessRoleByUserId(user.getId()).getSuccess();
        boolean isMonitoringOfficer = monitoringOfficerRestService.isMonitoringOfficer(user.getId()).getSuccess();
        boolean isApplicant = processRoleResources.stream().map(ProcessRoleResource::getRole)
                .anyMatch(Role::isKta);

        if (isMonitoringOfficer) {
            dashboardRoles.add(MONITORING_OFFICER);
        }
        if (isApplicant) {
            dashboardRoles.add(APPLICANT);
        }

        dashboardRoles.add(ASSESSOR);
    }

    private String viewDashboardSelection(HttpServletRequest request, Model model, Set<Role> roles) {
        if (roles.size() == 1) {
            return navigationUtils.getRedirectToDashboardUrlForRole(roles.stream().findAny().get());
        }

        List<DashboardPanel> dashboardPanels = simpleMap(roles, role -> createDashboardPanelForRole(request, role));
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