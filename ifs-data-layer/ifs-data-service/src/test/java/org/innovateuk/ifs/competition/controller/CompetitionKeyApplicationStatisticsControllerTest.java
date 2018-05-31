package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyApplicationStatisticsService;
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
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyApplicationStatisticsResourceBuilder.newCompetitionClosedKeyApplicationStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyApplicationStatisticsResourceBuilder.newCompetitionFundedKeyApplicationStatisticsResource;

import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyApplicationStatisticsResourceBuilder.newCompetitionOpenKeyApplicationStatisticsResource;
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

public class CompetitionKeyApplicationStatisticsControllerTest extends
        BaseControllerMockMVCTest<CompetitionKeyApplicationStatisticsController> {

    @Mock
    private CompetitionKeyApplicationStatisticsService competitionKeyApplicationStatisticsServiceMock;

    @Mock
    private InterviewStatisticsService interviewStatisticsServiceMock;

    @Mock
    private ReviewStatisticsService reviewStatisticsServiceMock;

    @Override
    protected CompetitionKeyApplicationStatisticsController supplyControllerUnderTest() {
        return new CompetitionKeyApplicationStatisticsController();
    }

    @Test
    public void getOpenKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionOpenKeyApplicationStatisticsResource keyStatisticsResource =
                newCompetitionOpenKeyApplicationStatisticsResource().build();
        when(competitionKeyApplicationStatisticsServiceMock.getOpenKeyStatisticsByCompetition(competitionId)).thenReturn
                (serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-application-statistics/{id}/open", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionClosedKeyApplicationStatisticsResource keyStatisticsResource =
                newCompetitionClosedKeyApplicationStatisticsResource()
                .build();
        when(competitionKeyApplicationStatisticsServiceMock.getClosedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-application-statistics/{id}/closed", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getFundedKeyStatistics() throws Exception {
        final long competitionId = 1L;

        CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource =
                newCompetitionFundedKeyApplicationStatisticsResource().build();
        when(competitionKeyApplicationStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/funded", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(keyStatisticsResource)));
    }

    @Test
    public void getReviewStatistics() throws Exception {
        final long competitionId = 1L;

        ReviewKeyStatisticsResource reviewKeyStatisticsResource = newReviewKeyStatisticsResource().build();
        when(reviewStatisticsServiceMock.getReviewPanelKeyStatistics(competitionId)).thenReturn(serviceSuccess
                (reviewKeyStatisticsResource));

        mockMvc.perform(get("/competition-application-statistics/{id}/review", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(reviewKeyStatisticsResource)));
    }

    @Test
    public void getReviewInviteStatistics() throws Exception {
        final long competitionId = 1L;

        ReviewInviteStatisticsResource reviewInviteStatisticsResource = newReviewInviteStatisticsResource().build();
        when(reviewStatisticsServiceMock.getReviewInviteStatistics(competitionId)).thenReturn(serviceSuccess
                (reviewInviteStatisticsResource));

        mockMvc.perform(get("/competition-application-statistics/{id}/review-invites", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(reviewInviteStatisticsResource)));
    }

    @Test
    public void getInterviewAssignmentStatistics() throws Exception {
        final long competitionId = 1L;

        InterviewAssignmentKeyStatisticsResource interviewAssignmentKeyStatisticsResource =
                newInterviewAssignmentKeyStatisticsResource().build();
        when(interviewStatisticsServiceMock.getInterviewAssignmentPanelKeyStatistics(competitionId)).thenReturn(
                serviceSuccess(interviewAssignmentKeyStatisticsResource));

        mockMvc.perform(get("/competition-application-statistics/{id}/interview-assignment", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(interviewAssignmentKeyStatisticsResource)));
    }

    @Test
    public void getInterviewInviteStatistics() throws Exception {
        final long competitionId = 1L;

        InterviewInviteStatisticsResource interviewInviteStatisticsResource = newInterviewInviteStatisticsResource()
                .build();
        when(interviewStatisticsServiceMock.getInterviewInviteStatistics(competitionId)).thenReturn(serviceSuccess
                (interviewInviteStatisticsResource));

        mockMvc.perform(get("/competition-application-statistics/{id}/interview-invites", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(interviewInviteStatisticsResource)));
    }

    @Test
    public void getInterviewStatistics() throws Exception {
        final long competitionId = 1L;

        InterviewStatisticsResource interviewInviteStatisticsResource = newInterviewStatisticsResource().build();
        when(interviewStatisticsServiceMock.getInterviewStatistics(competitionId)).thenReturn(serviceSuccess(interviewInviteStatisticsResource));

        mockMvc.perform(get("/competition-application-statistics/{id}/interview", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(interviewInviteStatisticsResource)));
    }
}