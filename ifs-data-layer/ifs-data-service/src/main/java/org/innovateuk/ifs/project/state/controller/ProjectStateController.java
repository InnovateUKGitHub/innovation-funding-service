package org.innovateuk.ifs.project.state.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.project.state.transactional.ProjectStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ProjectStateController exposes Project state data and operations through a REST API.
 */
@RestController
@RequestMapping("/project")
public class ProjectStateController {

    private ProjectStateService projectStateService;

    @Autowired
    public ProjectStateController(ProjectStateService projectStateService) {
        this.projectStateService = projectStateService;
    }

    @PostMapping("/{projectId}/withdraw")
    public RestResult<Void> withdrawProject(@PathVariable("projectId") final long projectId) {
        return projectStateService.withdrawProject(projectId).toPostWithBodyResponse();
    }

    @PostMapping("/{projectId}/handle-offline")
    public RestResult<Void> handleProjectOffline(@PathVariable("projectId") final long projectId) {
        return projectStateService.handleProjectOffline(projectId).toPostWithBodyResponse();
    }

    @PostMapping("/{projectId}/complete-offline")
    public RestResult<Void> completeProjectOffline(@PathVariable("projectId") final long projectId) {
        return projectStateService.completeProjectOffline(projectId).toPostWithBodyResponse();
    }

    @PostMapping("/{projectId}/on-hold")
    public RestResult<Void> putProjectOnHold(@PathVariable("projectId") final long projectId,
                                             @RequestBody OnHoldReasonResource reason) {
        return projectStateService.putProjectOnHold(projectId, reason).toPostWithBodyResponse();
    }

    @PostMapping("/{projectId}/resume")
    public RestResult<Void> resumeProject(@PathVariable("projectId") final long projectId) {
        return projectStateService.resumeProject(projectId).toPostWithBodyResponse();
    }
}
