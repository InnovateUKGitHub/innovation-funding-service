package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerDashboardViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/monitoring-officer/dashboard")
@Controller
@SecuredBySpring(value = "Controller", description = "Each monitoring officer has permission to view their own dashboard",
        securedType = MonitoringOfficerDashboardController.class)
@PreAuthorize("hasAnyAuthority('monitoring_officer')")
public class MonitoringOfficerDashboardController {

    private final MonitoringOfficerDashboardViewModelPopulator monitoringOfficerDashboardViewModelPopulator;

    public MonitoringOfficerDashboardController(MonitoringOfficerDashboardViewModelPopulator monitoringOfficerDashboardViewModelPopulator) {
        this.monitoringOfficerDashboardViewModelPopulator = monitoringOfficerDashboardViewModelPopulator;
    }

    @GetMapping
    public String viewDashboard(Model model,
                                UserResource user) {
        model.addAttribute("model", monitoringOfficerDashboardViewModelPopulator.populate(user));
        return "monitoring-officer/dashboard";
    }

}
