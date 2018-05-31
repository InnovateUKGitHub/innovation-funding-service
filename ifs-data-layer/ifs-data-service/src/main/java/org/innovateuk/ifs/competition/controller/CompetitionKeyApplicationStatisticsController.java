package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyApplicationStatisticsService;
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
@RequestMapping({"/competition-application-statistics/{id}"})
public class CompetitionKeyApplicationStatisticsController {

    private CompetitionKeyApplicationStatisticsService competitionKeyApplicationStatisticsService;
    private ReviewStatisticsService reviewStatisticsService;
    private InterviewStatisticsService interviewStatisticsService;

    public CompetitionKeyApplicationStatisticsController() {
    }

    @Autowired
    public CompetitionKeyApplicationStatisticsController(CompetitionKeyApplicationStatisticsService competitionKeyApplicationStatisticsService,
                                                         ReviewStatisticsService reviewStatisticsService,
                                                         InterviewStatisticsService interviewStatisticsService) {
        this.competitionKeyApplicationStatisticsService = competitionKeyApplicationStatisticsService;
        this.reviewStatisticsService = reviewStatisticsService;
        this.interviewStatisticsService = interviewStatisticsService;
    }

    @GetMapping("/open")
    public RestResult<CompetitionOpenKeyApplicationStatisticsResource> getOpenKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyApplicationStatisticsService.getOpenKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping("/closed")
    public RestResult<CompetitionClosedKeyApplicationStatisticsResource> getClosedKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyApplicationStatisticsService.getClosedKeyStatisticsByCompetition(id).toGetResponse();
    }

    @GetMapping("/funded")
    public RestResult<CompetitionFundedKeyApplicationStatisticsResource> getFundedKeyStatistics(@PathVariable("id") long id) {
        return competitionKeyApplicationStatisticsService.getFundedKeyStatisticsByCompetition(id).toGetResponse();
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