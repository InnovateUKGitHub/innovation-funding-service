package org.innovateuk.ifs.project.monitoring.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerDashboardPageResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle RESTful services related to project monitoring officers
 */
@RestController
@RequestMapping("/monitoring-officer")
public class MonitoringOfficerController {

    private MonitoringOfficerService monitoringOfficerService;
    private static final String DEFAULT_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "10";


    public MonitoringOfficerController(MonitoringOfficerService monitoringOfficerService) {
        this.monitoringOfficerService = monitoringOfficerService;
    }

    @GetMapping("/find-all")
    public RestResult<List<SimpleUserResource>> findAll() {
        return monitoringOfficerService.findAll().toGetResponse();
    }

    @GetMapping("/find-all-ktp")
    public RestResult<List<SimpleUserResource>> findAllKtp() {
        return monitoringOfficerService.findAllKtp().toGetResponse();
    }

    @GetMapping("/find-all-non-ktp")
    public RestResult<List<SimpleUserResource>> findAllNonKtp() {
        return monitoringOfficerService.findAllNonKtp().toGetResponse();
    }

    @GetMapping("/{userId}")
    public RestResult<MonitoringOfficerAssignmentResource> getProjectMonitoringOfficer(@PathVariable long userId) {
        return monitoringOfficerService.getProjectMonitoringOfficer(userId).toGetResponse();
    }

    @PostMapping("/{userId}/assign/{projectId}")
    public RestResult<Void> assignProjectToMonitoringOfficer(@PathVariable long userId, @PathVariable long projectId) {
        return monitoringOfficerService.assignProjectToMonitoringOfficer(userId, projectId).toPostResponse();
    }

    @PostMapping("/{userId}/unassign/{projectId}")
    public RestResult<Void> unassignProjectFromMonitoringOfficer(@PathVariable long userId, @PathVariable long projectId) {
        return monitoringOfficerService.unassignProjectFromMonitoringOfficer(userId, projectId).toPostResponse();
    }

    @GetMapping("{userId}/projects")
    public RestResult<List<ProjectResource>> getMonitoringOfficerProjects(@PathVariable final long userId) {
        return monitoringOfficerService.getMonitoringOfficerProjects(userId).toGetResponse();
    }

    @GetMapping("{userId}/filter-projects")
    public RestResult<MonitoringOfficerDashboardPageResource> filterMonitoringOfficerProjects(@PathVariable final long userId,
                                                                                              @RequestParam(value = "keywordSearch", required = false, defaultValue = "") String keywordSearch,
                                                                                              @RequestParam(value = "projectInSetup", required = false, defaultValue = "false") boolean projectInSetup,
                                                                                              @RequestParam(value = "previousProject", required = false, defaultValue = "false") boolean previousProject,
                                                                                              @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int pageNumber,
                                                                                              @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return monitoringOfficerService.filterMonitoringOfficerProjects(userId, keywordSearch, projectInSetup, previousProject,pageNumber,pageSize).toGetResponse();
    }

    @GetMapping("/project/{projectId}")
    public RestResult<MonitoringOfficerResource> findMonitoringOfficerForProject(@PathVariable final long projectId) {
        return monitoringOfficerService.findMonitoringOfficerForProject(projectId).toGetResponse();
    }

    @GetMapping("/project/{projectId}/is-monitoring-officer/{userId}")
    public RestResult<Boolean> isMonitoringOfficerOnProject(@PathVariable final long projectId,  @PathVariable final long userId) {
        return monitoringOfficerService.isMonitoringOfficerOnProject(projectId, userId).toGetResponse();
    }

    @GetMapping("/is-monitoring-officer/{userId}")
    public RestResult<Boolean> isMonitoringOfficer(@PathVariable final long userId) {
        return monitoringOfficerService.isMonitoringOfficer(userId).toGetResponse();
    }

    @PostMapping("/{projectId}/notify/{userId}")
    public RestResult<Void> sendDocumentReviewNotification(@PathVariable long projectId, @PathVariable long userId) {
        return monitoringOfficerService.sendDocumentReviewNotification(projectId, userId).toPostResponse();
    }

}