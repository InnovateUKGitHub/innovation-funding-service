package com.worth.ifs.project.status.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.transactional.ProjectStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProjectStatusController exposes status information about projects.
 */
@RestController
public class ProjectStatusController {
    @Autowired
    private ProjectStatusService projectStatusService;

    @RequestMapping("/project/competition/{competitionId}")
    public RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(@PathVariable final Long competitionId){
        return projectStatusService.getCompetitionStatus(competitionId).toGetResponse();
    }
}
