package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoringofficer.service.LegacyMonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.LegacyMonitoringOfficerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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
public class LegacyMonitoringOfficerController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private LegacyMonitoringOfficerRestService monitoringOfficerService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_MONITORING_OFFICER_SECTION')")
    @GetMapping
    public String viewMonitoringOfficer(@P("projectId")@PathVariable("projectId") Long projectId, Model model) {

        LegacyMonitoringOfficerViewModel viewModel = getMonitoringOfficerViewModel(projectId);
        model.addAttribute("model", viewModel);
        return "project/monitoring-officer";
    }

    @PreAuthorize("hasPermission(#projectId,'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_MONITORING_OFFICER_SECTION')")
    @GetMapping("/readonly")
    public String viewMonitoringOfficerInReadOnly(@P("projectId")@PathVariable("projectId") Long projectId, Model model) {

        LegacyMonitoringOfficerViewModel viewModel = getMonitoringOfficerViewModel(projectId);
        model.addAttribute("model", viewModel);
        model.addAttribute("readOnlyView", true);
        return "project/monitoring-officer";
    }


    private LegacyMonitoringOfficerViewModel getMonitoringOfficerViewModel(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        Optional<LegacyMonitoringOfficerResource> monitoringOfficer = monitoringOfficerService.getMonitoringOfficerForProject(projectId).getOptionalSuccessObject();
        return new LegacyMonitoringOfficerViewModel(project, monitoringOfficer);
    }
}
