package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.innovateuk.ifs.interview.transactional.InterviewStatisticsService;
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
@RestController
@RequestMapping({"/competition-statistics/{id}"})
public class CompetitionKeyStatisticsController {

    private CompetitionKeyStatisticsService competitionKeyStatisticsService;
    private ReviewStatisticsService reviewStatisticsService;
    private InterviewStatisticsService interviewStatisticsService;

    public CompetitionKeyStatisticsController() {
    }

    @Autowired
    public CompetitionKeyStatisticsController(CompetitionKeyStatisticsService competitionKeyStatisticsService,
                                              ReviewStatisticsService reviewStatisticsService,
                                              InterviewStatisticsService interviewStatisticsService) {
        this.competitionKeyStatisticsService = competitionKeyStatisticsService;
        this.reviewStatisticsService = reviewStatisticsService;
        this.interviewStatisticsService = interviewStatisticsService;
    }

    @GetMapping({"/ready-to-open"})
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

    @GetMapping({"/in-assessment"})
    public RestResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getInAssessmentKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping("/funded")
    public RestResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyStatisticsService.getFundedKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping({"/review"})
    public RestResult<ReviewKeyStatisticsResource> getReviewStatistics(@PathVariable("id") long id) {
        return reviewStatisticsService.getReviewPanelKeyStatistics(id).toGetResponse();
    }

    @GetMapping({"/review-invites"})
    public RestResult<ReviewInviteStatisticsResource> getReviewInviteStatistics(@PathVariable("id") long id) {
        return reviewStatisticsService.getReviewInviteStatistics(id).toGetResponse();
    }

    @GetMapping("/interview-assignment")
    public RestResult<InterviewAssignmentKeyStatisticsResource> getInterviewAssignmentStatistics(@PathVariable("id") long id) {
        return interviewStatisticsService.getInterviewAssignmentPanelKeyStatistics(id).toGetResponse();
    }

    @GetMapping("/interview-invites")
    public RestResult<InterviewInviteStatisticsResource> getInterviewInviteStatistics(@PathVariable("id") long id) {
        return interviewStatisticsService.getInterviewInviteStatistics(id).toGetResponse();
    }

    @GetMapping("/interview")
    public RestResult<InterviewStatisticsResource> getInterviewStatistics(@PathVariable("id") long id) {
        return interviewStatisticsService.getInterviewStatistics(id).toGetResponse();
    }

}