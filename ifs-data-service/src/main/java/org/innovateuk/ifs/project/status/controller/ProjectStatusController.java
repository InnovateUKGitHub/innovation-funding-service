package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.transactional.ProjectStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Optional.ofNullable;

/**
 * ProjectStatusController exposes status information about projects.
 */
@RestController
@RequestMapping("/project")
public class ProjectStatusController {
    @Autowired
    private ProjectStatusService projectStatusService;

    @GetMapping("/competition/{competitionId}")
    public RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(@PathVariable final Long competitionId){
        return projectStatusService.getCompetitionStatus(competitionId).toGetResponse();
    }

    @GetMapping("/{projectId}/team-status")
    public RestResult<ProjectTeamStatusResource> getTeamStatus(@PathVariable(value = "projectId") Long projectId,
                                                               @RequestParam(value = "filterByUserId", required = false) Long filterByUserId) {
        return projectStatusService.getProjectTeamStatus(projectId, ofNullable(filterByUserId)).toGetResponse();
    }

    @GetMapping("/{projectId}/status")
    public RestResult<ProjectStatusResource> getStatus(@PathVariable(value = "projectId") Long projectId) {
        return projectStatusService.getProjectStatusByProjectId(projectId).toGetResponse();
    }
}
