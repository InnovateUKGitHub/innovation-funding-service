package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.transactional.ProjectStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProjectStatusController exposes status information about projects.
 */
@RestController
public class ProjectStatusController {
    @Autowired
    private ProjectStatusService projectStatusService;

    @GetMapping("/project/competition/{competitionId}")
    public RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(@PathVariable final Long competitionId){
        return projectStatusService.getCompetitionStatus(competitionId).toGetResponse();
    }
}
