package org.innovateuk.ifs.project.monitoring.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle RESTful services related to project monitoring officers
 */
@RestController
@RequestMapping("/monitoring-officer")
public class MonitoringOfficerController {

    private MonitoringOfficerService projectMonitoringOfficerService;

    public MonitoringOfficerController(MonitoringOfficerService projectMonitoringOfficerService) {
        this.projectMonitoringOfficerService = projectMonitoringOfficerService;
    }

    @GetMapping("/find-all")
    public RestResult<List<MonitoringOfficerResource>> findAll() {
        return projectMonitoringOfficerService.findAll().toGetResponse();
    }

    @GetMapping("/{userId}")
    public RestResult<MonitoringOfficerResource> getProjectMonitoringOfficer(@PathVariable long userId) {
        return projectMonitoringOfficerService.getProjectMonitoringOfficer(userId).toGetResponse();
    }

    @PostMapping("/{userId}/assign/{projectId}")
    public RestResult<Void> assignProjectToMonitoringOfficer(@PathVariable long userId, @PathVariable long projectId) {
        return projectMonitoringOfficerService.assignProjectToMonitoringOfficer(userId, projectId).toPostResponse();
    }

    @PostMapping("/{userId}/unassign/{projectId}")
    public RestResult<Void> unassignProjectFromMonitoringOfficer(@PathVariable long userId, @PathVariable long projectId) {
        return projectMonitoringOfficerService.unassignProjectFromMonitoringOfficer(userId, projectId).toPostResponse();
    }

    @GetMapping("{userId}/projects")
    public RestResult<List<ProjectResource>> getMonitoringOfficerProjects(@PathVariable("userId") final long userId) {
        return projectMonitoringOfficerService.getMonitoringOfficerProjects(userId).toGetResponse();
    }

}