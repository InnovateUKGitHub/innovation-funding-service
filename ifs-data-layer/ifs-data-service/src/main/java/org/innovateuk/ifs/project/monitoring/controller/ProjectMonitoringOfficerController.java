package org.innovateuk.ifs.project.monitoring.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.ProjectMonitoringOfficerService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to handle RESTful services related to inviting project monitoring officers
 */
@RestController
@RequestMapping("/project-monitoring-officer/{userId}")
public class ProjectMonitoringOfficerController {

    private ProjectMonitoringOfficerService projectMonitoringOfficerService;

    public ProjectMonitoringOfficerController(ProjectMonitoringOfficerService projectMonitoringOfficerService) {
        this.projectMonitoringOfficerService = projectMonitoringOfficerService;
    }

    @GetMapping
    public RestResult<ProjectMonitoringOfficerResource> getProjectMonitoringOfficer(@PathVariable long userId) {
        return projectMonitoringOfficerService.getProjectMonitoringOfficer(userId).toGetResponse();
    }

    @PostMapping("/assign/{projectId}")
    public RestResult<Void> assignProjectToMonitoringOfficer(@PathVariable long userId, @PathVariable long projectId) {
        return projectMonitoringOfficerService.assignProjectToMonitoringOfficer(userId, projectId).toPostResponse();
    }

    @PostMapping("/unassign/{projectId}")
    public RestResult<Void> unassignProjectFromMonitoringOfficer(@PathVariable long userId, @PathVariable long projectId) {
        return projectMonitoringOfficerService.unassignProjectFromMonitoringOfficer(userId, projectId).toPostResponse();
    }
}