package org.innovateuk.ifs.analytics.controller;

import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/application/{applicationId}/user-roles")
    public RestResult<List<Role>> getRolesByApplicationIdForCurrentUser(@PathVariable("applicationId") long applicationId) {
        return googleAnalyticsDataLayerService.getRolesByApplicationIdForCurrentUser(applicationId).toGetResponse();
    }

    @GetMapping("/competition/{competitionId}/competition-name")
    public RestResult<String> getCompetitionName(@PathVariable("competitionId") long competitionId) {
        return googleAnalyticsDataLayerService.getCompetitionName(competitionId).toGetResponse();
    }

    @GetMapping("/project/{projectId}/competition-name")
    public RestResult<String> getCompetitionNameForProject(@PathVariable("projectId") long projectId) {
        return googleAnalyticsDataLayerService.getCompetitionNameByProjectId(projectId).toGetResponse();
    }

    @GetMapping("/project/{projectId}/user-roles")
    public RestResult<List<Role>> getRolesByProjectIdForCurrentUser(@PathVariable("projectId") long projectId) {
        return googleAnalyticsDataLayerService.getRolesByProjectIdForCurrentUser(projectId).toGetResponse();
    }

    @GetMapping("/assessment/{assessmentId}/competition-name")
    public RestResult<String> getCompetitionNameForAssessment(@PathVariable("assessmentId") long assessmentId) {
        return googleAnalyticsDataLayerService.getCompetitionNameByAssessmentId(assessmentId).toGetResponse();
    }

    @GetMapping("/project/{projectId}/application-id")
    public RestResult<Long> getApplicationIdForProject(@PathVariable("projectId") long projectId) {
        return googleAnalyticsDataLayerService.getApplicationIdForProject(projectId).toGetResponse();
    }
}