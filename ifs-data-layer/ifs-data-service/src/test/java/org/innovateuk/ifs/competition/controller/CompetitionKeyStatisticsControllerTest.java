package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.innovateuk.ifs.interview.transactional.InterviewStatisticsService;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.review.transactional.ReviewStatisticsService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder.newCompetitionFundedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder.newCompetitionReadyToOpenKeyStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewInviteStatisticsResourceBuilder.newInterviewInviteStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder.newInterviewStatisticsResource;
import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;
import static org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder.newReviewKeyStatisticsResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class CompetitionKeyStatisticsControllerTest extends BaseControllerMockMVCTest<CompetitionKeyStatisticsController> {

    @Mock
    private CompetitionKeyStatisticsService competitionKeyStatisticsServiceMock;

    @Mock
    private InterviewStatisticsService interviewStatisticsServiceMock;

    @Mock
    private ReviewStatisticsService reviewStatisticsServiceMock;
    @Override
    protected CompetitionKeyStatisticsController supplyControllerUnderTest() {
        return new CompetitionKeyStatisticsController();
    }

    @Test
    public void getReadyToOpenKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource = newCompetitionReadyToOpenKeyStatisticsResource().build();

        when(competitionKeyStatisticsServiceMock.getReadyToOpenKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/ready-to-open", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getOpenKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionOpenKeyStatisticsResource keyStatisticsResource = newCompetitionOpenKeyStatisticsResource().build();
        when(competitionKeyStatisticsServiceMock.getOpenKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/open", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionClosedKeyStatisticsResource keyStatisticsResource = newCompetitionClosedKeyStatisticsResource().build();
        when(competitionKeyStatisticsServiceMock.getClosedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/closed", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getInAssessmentKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = newCompetitionInAssessmentKeyStatisticsResource().build();
        when(competitionKeyStatisticsServiceMock.getInAssessmentKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/in-assessment", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getFundedKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionFundedKeyStatisticsResource keyStatisticsResource = newCompetitionFundedKeyStatisticsResource().build();
        when(competitionKeyStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/funded", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getReviewStatistics() throws Exception {
        final long competitionId = 1L;

        ReviewKeyStatisticsResource reviewKeyStatisticsResource = newReviewKeyStatisticsResource().build();
        when(reviewStatisticsServiceMock.getReviewPanelKeyStatistics(competitionId)).thenReturn(serviceSuccess(reviewKeyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/review", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(reviewKeyStatisticsResource)));
    }

    @Test
    public void getReviewInviteStatistics() throws Exception {
        final long competitionId = 1L;

        ReviewInviteStatisticsResource reviewInviteStatisticsResource = newReviewInviteStatisticsResource().build();
        when(reviewStatisticsServiceMock.getReviewInviteStatistics(competitionId)).thenReturn(serviceSuccess(reviewInviteStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/review-invites", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(reviewInviteStatisticsResource)));
    }

    @Test
    public void getInterviewAssignmentStatistics() throws Exception {
        final long competitionId = 1L;

        InterviewAssignmentKeyStatisticsResource interviewAssignmentKeyStatisticsResource = newInterviewAssignmentKeyStatisticsResource().build();
        when(interviewStatisticsServiceMock.getInterviewAssignmentPanelKeyStatistics(competitionId)).thenReturn(serviceSuccess(interviewAssignmentKeyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/interview-assignment", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(interviewAssignmentKeyStatisticsResource)));
    }

    @Test
    public void getInterviewInviteStatistics() throws Exception {
        final long competitionId = 1L;

        InterviewInviteStatisticsResource interviewInviteStatisticsResource = newInterviewInviteStatisticsResource().build();
        when(interviewStatisticsServiceMock.getInterviewInviteStatistics(competitionId)).thenReturn(serviceSuccess(interviewInviteStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/interview-invites", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(interviewInviteStatisticsResource)));
    }

    @Test
    public void getInterviewStatistics() throws Exception {
        final long competitionId = 1L;

        InterviewStatisticsResource interviewInviteStatisticsResource = newInterviewStatisticsResource().build();
        when(interviewStatisticsServiceMock.getInterviewStatistics(competitionId)).thenReturn(serviceSuccess(interviewInviteStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/interview", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(interviewInviteStatisticsResource)));
    }
}