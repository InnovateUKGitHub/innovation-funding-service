package org.innovateuk.ifs.project.monitoring.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.ProjectMonitoringOfficerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle RESTful services related to inviting project monitoring officers
 */
@RestController
@RequestMapping("/project-monitoring-officer")
public class ProjectMonitoringOfficerController {

    private ProjectMonitoringOfficerService projectMonitoringOfficerService;

    public ProjectMonitoringOfficerController(ProjectMonitoringOfficerService projectMonitoringOfficerService) {
        this.projectMonitoringOfficerService = projectMonitoringOfficerService;
    }

    @GetMapping("/{projectMonitoringOfficerId}")
    public RestResult<ProjectMonitoringOfficerResource> getProjectMonitoringOfficer(@PathVariable("projectMonitoringOfficerId") long projectMonitoringOfficerId) {
        return projectMonitoringOfficerService.getProjectMonitoringOfficer(projectMonitoringOfficerId).toGetResponse();
    }
}