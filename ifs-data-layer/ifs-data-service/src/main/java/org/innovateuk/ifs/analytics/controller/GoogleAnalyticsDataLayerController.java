package org.innovateuk.ifs.analytics.controller;

import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.domain.ProjectParticipantRole;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class GoogleAnalyticsDataLayerController {

    @Autowired
    private GoogleAnalyticsDataLayerService googleAnalyticsDataLayerService;

    @GetMapping("/application/{applicationId}/competition-name")
    public RestResult<String> getCompetitionNameForApplication(@PathVariable("applicationId") long applicationId) {
        return googleAnalyticsDataLayerService.getCompetitionNameByApplicationId(applicationId).toGetResponse();
    }

    @GetMapping("/application/{applicationId}/application-roles")
    public RestResult<List<Role>> getApplicationRolesById(@PathVariable("applicationId") long applicationId) {
        return googleAnalyticsDataLayerService.getApplicationRolesById(applicationId).toGetResponse();
    }

    @GetMapping("/competition/{competitionId}/competition-name")
    public RestResult<String> getCompetitionName(@PathVariable("competitionId") long competitionId) {
        return googleAnalyticsDataLayerService.getCompetitionName(competitionId).toGetResponse();
    }

    @GetMapping("/project/{projectId}/competition-name")
    public RestResult<String> getCompetitionNameForProject(@PathVariable("projectId") long projectId) {
        return googleAnalyticsDataLayerService.getCompetitionNameByProjectId(projectId).toGetResponse();
    }

    @GetMapping("/project/{projectId}/project-roles")
    public RestResult<List<Role>> getProjectRolesById(@PathVariable("projectId") long projectId) {
        return googleAnalyticsDataLayerService.getProjectRolesById(projectId).toGetResponse();
    }

    @GetMapping("/assessment/{assessmentId}/competition-name")
    public RestResult<String> getCompetitionNameForAssessment(@PathVariable("assessmentId") long assessmentId) {
        return googleAnalyticsDataLayerService.getCompetitionNameByAssessmentId(assessmentId).toGetResponse();
    }
}