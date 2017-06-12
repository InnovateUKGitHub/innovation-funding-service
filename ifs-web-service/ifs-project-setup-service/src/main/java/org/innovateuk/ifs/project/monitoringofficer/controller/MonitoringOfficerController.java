package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.monitoringofficer.MonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerViewModel;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

/**
 * Controller for the Partners' assigned Monitoring Officer page
 */
@Controller
@RequestMapping("/project/{projectId}/monitoring-officer")
public class MonitoringOfficerController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_MONITORING_OFFICER_SECTION')")
    @GetMapping
    public String viewMonitoringOfficer(@PathVariable("projectId") Long projectId, Model model) {

        MonitoringOfficerViewModel viewModel = getMonitoringOfficerViewModel(projectId);
        model.addAttribute("model", viewModel);
        return "project/monitoring-officer";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_MONITORING_OFFICER_SECTION')")
    @GetMapping("/readonly")
    public String viewMonitoringOfficerInReadOnly(@PathVariable("projectId") Long projectId, Model model) {

        MonitoringOfficerViewModel viewModel = getMonitoringOfficerViewModel(projectId);
        model.addAttribute("model", viewModel);
        model.addAttribute("readOnlyView", true);
        return "project/monitoring-officer";
    }


    private MonitoringOfficerViewModel getMonitoringOfficerViewModel(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        Optional<MonitoringOfficerResource> monitoringOfficer = monitoringOfficerService.getMonitoringOfficerForProject(projectId);
        return new MonitoringOfficerViewModel(project, monitoringOfficer);
    }


    public static void main(String[] args) {
        System.out.println("Came here");

        String text = "comp_admin";



        List<String> roles = CollectionFunctions.simpleMap(text.split("&"), (s) -> s.trim());

        System.out.println("Finished");

    }
}
