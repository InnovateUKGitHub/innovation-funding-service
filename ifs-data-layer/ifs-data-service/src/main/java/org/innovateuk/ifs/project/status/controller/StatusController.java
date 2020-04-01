package org.innovateuk.ifs.project.status.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.ProjectStatusPageResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.transactional.InternalUserProjectStatusService;
import org.innovateuk.ifs.project.status.transactional.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * ProjectStatusController exposes status information about projects.
 */
@RestController
@RequestMapping("/project")
public class StatusController {
    @Autowired
    private StatusService statusService;
    @Autowired
    private InternalUserProjectStatusService internalUserProjectStatusService;

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "5";

    @GetMapping("/competition/{competitionId}")
    public RestResult<ProjectStatusPageResource> getCompetitionStatus(@PathVariable final long competitionId,
                                                                      @RequestParam(name = "applicationSearchString", defaultValue = "") String applicationSearchString,
                                                                      @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                                                      @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size) {
        return internalUserProjectStatusService.getCompetitionStatus(competitionId, StringUtils.trim(applicationSearchString), page, size).toGetResponse();
    }

    @GetMapping("/previous/competition/{competitionId}")
    public RestResult<List<ProjectStatusResource>> getPreviousCompetitionStatus(@PathVariable final long competitionId) {
        return internalUserProjectStatusService.getPreviousCompetitionStatus(competitionId).toGetResponse();
    }

    @GetMapping("/{projectId}/team-status")
    public RestResult<ProjectTeamStatusResource> getTeamStatus(@PathVariable(value = "projectId") Long projectId,
                                                               @RequestParam(value = "filterByUserId", required = false) Long filterByUserId) {
        return statusService.getProjectTeamStatus(projectId, ofNullable(filterByUserId)).toGetResponse();
    }

    @GetMapping("/{projectId}/status")
    public RestResult<ProjectStatusResource> getStatus(@PathVariable(value = "projectId") Long projectId) {
        return internalUserProjectStatusService.getProjectStatusByProjectId(projectId).toGetResponse();
    }
}
