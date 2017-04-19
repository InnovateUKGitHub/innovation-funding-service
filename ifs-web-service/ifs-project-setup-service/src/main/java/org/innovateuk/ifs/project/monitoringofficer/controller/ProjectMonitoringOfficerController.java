package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.monitoringofficer.ProjectMonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.ProjectMonitoringOfficerViewModel;
import org.innovateuk.ifs.project.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * Controller for the Partners' assigned Monitoring Officer page
 */
@Controller
@RequestMapping("/project/{projectId}/monitoring-officer")
public class ProjectMonitoringOfficerController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMonitoringOfficerService projectMonitoringOfficerService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_MONITORING_OFFICER_SECTION')")
    @GetMapping
    public String viewMonitoringOfficer(@PathVariable("projectId") Long projectId, Model model) {

        ProjectMonitoringOfficerViewModel viewModel = getMonitoringOfficerViewModel(projectId);
        model.addAttribute("model", viewModel);
        return "project/monitoring-officer";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_MONITORING_OFFICER_SECTION')")
    @GetMapping("/readonly")
    public String viewMonitoringOfficerInReadOnly(@PathVariable("projectId") Long projectId, Model model) {

        ProjectMonitoringOfficerViewModel viewModel = getMonitoringOfficerViewModel(projectId);
        model.addAttribute("model", viewModel);
        model.addAttribute("readOnlyView", true);
        return "project/monitoring-officer";
    }


    private ProjectMonitoringOfficerViewModel getMonitoringOfficerViewModel(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        Optional<MonitoringOfficerResource> monitoringOfficer = projectMonitoringOfficerService.getMonitoringOfficerForProject(projectId);
        return new ProjectMonitoringOfficerViewModel(project, monitoringOfficer);
    }
}
