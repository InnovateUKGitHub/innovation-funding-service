package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.CompetitionKeyApplicationStatisticsController;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyApplicationStatisticsService;
import org.innovateuk.ifs.interview.transactional.InterviewStatisticsService;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.review.transactional.ReviewStatisticsService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionClosedKeyApplicationStatisticsResourceDocs.competitionClosedKeyApplicationStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionClosedKeyApplicationStatisticsResourceDocs.competitionClosedKeyApplicationStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionFundedKeyApplicationStatisticsResourceDocs.competitionFundedKeyApplicationStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionFundedKeyApplicationStatisticsResourceDocs.competitionFundedKeyApplicationStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionOpenKeyApplicationStatisticsResourceDocs.competitionOpenKeyApplicationStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionOpenKeyApplicationStatisticsResourceDocs.competitionOpenKeyApplicationStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentKeyStatisticsResourceDocs.interviewAssignmentKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.InterviewAssignmentKeyStatisticsResourceDocs.interviewAssignmentKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.InterviewInviteStatisticsResourceDocs.interviewInviteStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.InterviewInviteStatisticsResourceDocs.interviewInviteStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.InterviewStatisticsResourceDocs.INTERVIEW_STATISTICS_RESOURCE_BUILDER;
import static org.innovateuk.ifs.documentation.InterviewStatisticsResourceDocs.INTERVIEW_STATISTICS_RESOURCE_FIELDS;
import static org.innovateuk.ifs.documentation.ReviewInviteStatisticsResourceDocs.reviewInviteStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.ReviewInviteStatisticsResourceDocs.reviewInviteStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.ReviewKeyStatisticsResourceDocs.reviewKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.ReviewKeyStatisticsResourceDocs.reviewKeyStatisticsResourceFields;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionKeyApplicationStatisticsControllerDocumentation extends
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
        long competitionId = 1L;
        CompetitionOpenKeyApplicationStatisticsResource keyStatisticsResource =
                competitionOpenKeyApplicationStatisticsResourceBuilder.build();

        when(competitionKeyApplicationStatisticsServiceMock.getOpenKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-application-statistics/{id}/open", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-application-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionOpenKeyApplicationStatisticsResourceFields)
                ));

        verify(competitionKeyApplicationStatisticsServiceMock, only()).getOpenKeyStatisticsByCompetition(competitionId);
    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionClosedKeyApplicationStatisticsResource keyStatisticsResource =
                competitionClosedKeyApplicationStatisticsResourceBuilder.build();

        when(competitionKeyApplicationStatisticsServiceMock.getClosedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-application-statistics/{id}/closed", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-application-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionClosedKeyApplicationStatisticsResourceFields)
                ));

        verify(competitionKeyApplicationStatisticsServiceMock, only()).getClosedKeyStatisticsByCompetition
                (competitionId);
    }

    @Test
    public void getFundedKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource =
                competitionFundedKeyApplicationStatisticsResourceBuilder.build();

        when(competitionKeyApplicationStatisticsServiceMock.getFundedKeyStatisticsByCompetition(competitionId))
                .thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-application-statistics/{id}/funded", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-application-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionFundedKeyApplicationStatisticsResourceFields)
                ));

        verify(competitionKeyApplicationStatisticsServiceMock, only()).getFundedKeyStatisticsByCompetition
                (competitionId);
    }

    @Test
    public void getReviewStatistics() throws Exception {
        long competitionId = 1L;
        ReviewKeyStatisticsResource reviewKeyStatisticsResource = reviewKeyStatisticsResourceBuilder.build();

        when(reviewStatisticsServiceMock.getReviewPanelKeyStatistics(competitionId))
                .thenReturn(serviceSuccess(reviewKeyStatisticsResource));
        mockMvc.perform(get("/competition-application-statistics/{id}/review", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-application-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(reviewKeyStatisticsResourceFields)
                ));

        verify(reviewStatisticsServiceMock, only()).getReviewPanelKeyStatistics(competitionId);
    }

    @Test
    public void getReviewInviteStatistics() throws Exception {
        long competitionId = 1L;
        ReviewInviteStatisticsResource reviewInviteStatisticsResource = reviewInviteStatisticsResourceBuilder.build();

        when(reviewStatisticsServiceMock.getReviewInviteStatistics(competitionId))
                .thenReturn(serviceSuccess(reviewInviteStatisticsResource));
        mockMvc.perform(get("/competition-application-statistics/{id}/review-invites", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-application-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(reviewInviteStatisticsResourceFields)
                ));

        verify(reviewStatisticsServiceMock, only()).getReviewInviteStatistics(competitionId);
    }

    @Test
    public void getInterviewAssignmentStatistics() throws Exception {
        long competitionId = 1L;
        when(interviewStatisticsServiceMock.getInterviewAssignmentPanelKeyStatistics(competitionId))
                .thenReturn(serviceSuccess(interviewAssignmentKeyStatisticsResourceBuilder.build()));

        mockMvc.perform(get("/competition-application-statistics/{id}/interview-assignment", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-application-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition")
                        ),
                        responseFields(interviewAssignmentKeyStatisticsResourceFields)
                ));

        verify(interviewStatisticsServiceMock, only()).getInterviewAssignmentPanelKeyStatistics(competitionId);
    }

    @Test
    public void getInterviewInviteStatistics() throws Exception {
        long competitionId = 1L;
        when(interviewStatisticsServiceMock.getInterviewInviteStatistics(competitionId))
                .thenReturn(serviceSuccess(interviewInviteStatisticsResourceBuilder.build()));

        mockMvc.perform(get("/competition-application-statistics/{id}/interview-invites", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-application-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition")
                        ),
                        responseFields(interviewInviteStatisticsResourceFields)
                ));

        verify(interviewStatisticsServiceMock, only()).getInterviewInviteStatistics(competitionId);
    }

    @Test
    public void getInterviewStatistics() throws Exception {
        long competitionId = 1L;
        when(interviewStatisticsServiceMock.getInterviewStatistics(competitionId)).thenReturn(serviceSuccess(INTERVIEW_STATISTICS_RESOURCE_BUILDER.build()));

        mockMvc.perform(get("/competition-application-statistics/{id}/interview", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-application-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition")
                        ),
                        responseFields(INTERVIEW_STATISTICS_RESOURCE_FIELDS)
                ));

        verify(interviewStatisticsServiceMock, only()).getInterviewStatistics(competitionId);
    }
}