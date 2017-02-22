package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * CompetitionKeyStatisticsController exposes the statistics for a {@link Competition}
 */
@RestController
@RequestMapping("/competitionStatistics/{id}")
public class CompetitionKeyStatisticsController {

    @Autowired
    private CompetitionKeyStatisticsService competitionKeyStatisticsService;

    @GetMapping(value ="/readyToOpen")
    public RestResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getReadyToOpenKeyStatisticsByCompetition(id).toGetResponse();
    }
    @GetMapping(value ="/open",method)
    public RestResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getOpenKeyStatisticsByCompetition(id).toGetResponse();
    }
    @GetMapping(value ="/closed")
    public RestResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getClosedKeyStatisticsByCompetition(id).toGetResponse();
    }
    @GetMapping(value ="/inAssessment")
    public RestResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getInAssessmentKeyStatisticsByCompetition(id).toGetResponse();
    }
    @GetMapping(value ="/funded")
    public RestResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getFundedKeyStatisticsByCompetition(id).toGetResponse();
    }
}
