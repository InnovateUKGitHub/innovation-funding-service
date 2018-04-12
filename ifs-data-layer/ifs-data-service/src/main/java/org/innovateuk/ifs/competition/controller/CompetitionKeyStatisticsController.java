package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.review.transactional.ReviewStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CompetitionKeyStatisticsController exposes the statistics for a {@link Competition}
 */
@ZeroDowntime(reference = "IFS-3308", description = "remove old mappings")
@RestController
@RequestMapping({"/competitionStatistics/{id}", "/competition-statistics/{id}"})
public class CompetitionKeyStatisticsController {

    private CompetitionKeyStatisticsService competitionKeyStatisticsService;
    private ReviewStatisticsService reviewStatisticsService;
    private AssessmentService assessmentService;


    // TODO remove our dependency on AssessmentService as we're not doing anything with assessments here
    // maybe we want review statistics controller, and interview statistics controller
    @Autowired
    public CompetitionKeyStatisticsController(CompetitionKeyStatisticsService competitionKeyStatisticsService,
                                              ReviewStatisticsService reviewStatisticsService,
                                              AssessmentService assessmentService) {
        this.competitionKeyStatisticsService = competitionKeyStatisticsService;
        this.reviewStatisticsService = reviewStatisticsService;
        this.assessmentService = assessmentService;
    }

    public CompetitionKeyStatisticsController() {

    }

    @GetMapping({"/readyToOpen", "/ready-to-open"})
    public RestResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getReadyToOpenKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping("/open")
    public RestResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getOpenKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping("/closed")
    public RestResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getClosedKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping({"/inAssessment", "/in-assessment"})
    public RestResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getInAssessmentKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping("/funded")
    public RestResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getFundedKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping({"/review", "/panel"})
    public RestResult<ReviewKeyStatisticsResource> getReviewStatistics(@PathVariable("id") long id) {
        return reviewStatisticsService.getAssessmentPanelKeyStatistics(id).toGetResponse();
    }

    @GetMapping({"/panelInvites", "/review-invites"})
    public RestResult<ReviewInviteStatisticsResource> getReviewInviteStatistics(@PathVariable("id") long id) {
        return reviewStatisticsService.getReviewInviteStatistics(id).toGetResponse();
    }

    @GetMapping("/interview-invites")
    public RestResult<InterviewInviteStatisticsResource> getInterviewInviteStatistics(@PathVariable("id") long id) {
        return assessmentService.getInterviewInviteStatistics(id).toGetResponse();
    }
}