package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.transactional.CompetitionKeyAssessmentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CompetitionKeyAssessmentStatisticsController exposes the statistics for a {@link Competition}
 */
@RestController
@RequestMapping({"/competition-assessment-statistics/{id}"})
public class CompetitionKeyAssessmentStatisticsController {

    private CompetitionKeyAssessmentStatisticsService competitionKeyAssessmentStatisticsService;

    public CompetitionKeyAssessmentStatisticsController() {
    }

    @Autowired
    public CompetitionKeyAssessmentStatisticsController(CompetitionKeyAssessmentStatisticsService
                                                                competitionKeyAssessmentStatisticsService) {
        this.competitionKeyAssessmentStatisticsService = competitionKeyAssessmentStatisticsService;
    }

    @GetMapping({"/ready-to-open"})
    public RestResult<CompetitionReadyToOpenKeyAssessmentStatisticsResource> getReadyToOpenKeyStatistics(
            @PathVariable("id") long id) {
        return competitionKeyAssessmentStatisticsService.getReadyToOpenKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping("/open")
    public RestResult<CompetitionOpenKeyAssessmentStatisticsResource> getOpenKeyStatistics(
            @PathVariable("id") long id) {
        return competitionKeyAssessmentStatisticsService.getOpenKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping("/closed")
    public RestResult<CompetitionClosedKeyAssessmentStatisticsResource> getClosedKeyStatistics(
            @PathVariable("id") long id) {
        return competitionKeyAssessmentStatisticsService.getClosedKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping({"/in-assessment"})
    public RestResult<CompetitionInAssessmentKeyAssessmentStatisticsResource> getInAssessmentKeyStatistics(
            @PathVariable("id") long id) {
        return competitionKeyAssessmentStatisticsService.getInAssessmentKeyStatisticsByCompetition(id).toGetResponse();
    }
}