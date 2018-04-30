package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.CompetitionKeyStatisticsController;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionReadyToOpenKeyStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.CompetitionClosedKeyStatisticsResourceDocs.competitionClosedKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionClosedKeyStatisticsResourceDocs.competitionClosedKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionInAssessmentKeyStatisticsResourceDocs.competitionInAssessmentKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionInAssessmentKeyStatisticsResourceDocs.competitionInAssessmentKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionOpenKeyStatisticsResourceDocs.competitionOpenKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionOpenKeyStatisticsResourceDocs.competitionOpenKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.CompetitionReadyToOpenKeyStatisticsResourceDocs.competitionReadyToOpenKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.CompetitionReadyToOpenKeyStatisticsResourceDocs.competitionReadyToOpenKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.InterviewAssignmentKeyStatisticsResourceDocs.interviewAssignmentKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.InterviewAssignmentKeyStatisticsResourceDocs.interviewAssignmentKeyStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.InterviewInviteStatisticsResourceDocs.interviewInviteStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.InterviewInviteStatisticsResourceDocs.interviewInviteStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.ReviewInviteStatisticsResourceDocs.reviewInviteStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.ReviewInviteStatisticsResourceDocs.reviewInviteStatisticsResourceFields;
import static org.innovateuk.ifs.documentation.ReviewKeyStatisticsResourceDocs.reviewKeyStatisticsResourceBuilder;
import static org.innovateuk.ifs.documentation.ReviewKeyStatisticsResourceDocs.reviewKeyStatisticsResourceFields;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionKeyStatisticsControllerDocumentation extends BaseControllerMockMVCTest<CompetitionKeyStatisticsController> {

    @Override
    protected CompetitionKeyStatisticsController supplyControllerUnderTest() {
        return new CompetitionKeyStatisticsController();
    }

    @Test
    public void getReadyToOpenKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource = competitionReadyToOpenKeyStatisticsResourceBuilder.build();

        when(competitionKeyStatisticsServiceMock.getReadyToOpenKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));

        mockMvc.perform(get("/competition-statistics/{id}/ready-to-open", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionReadyToOpenKeyStatisticsResourceFields)
                ));
    }

    @Test
    public void getOpenKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionOpenKeyStatisticsResource keyStatisticsResource = competitionOpenKeyStatisticsResourceBuilder.build();

        when(competitionKeyStatisticsServiceMock.getOpenKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-statistics/{id}/open", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionOpenKeyStatisticsResourceFields)
                ));

    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionClosedKeyStatisticsResource keyStatisticsResource = competitionClosedKeyStatisticsResourceBuilder.build();

        when(competitionKeyStatisticsServiceMock.getClosedKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-statistics/{id}/closed", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionClosedKeyStatisticsResourceFields)
                ));

    }

    @Test
    public void getInAssessmentKeyStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = competitionInAssessmentKeyStatisticsResourceBuilder.build();

        when(competitionKeyStatisticsServiceMock.getInAssessmentKeyStatisticsByCompetition(competitionId)).thenReturn(serviceSuccess(keyStatisticsResource));
        mockMvc.perform(get("/competition-statistics/{id}/inAssessment", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(competitionInAssessmentKeyStatisticsResourceFields)
                ));

    }

    @Test
    public void getReviewStatistics() throws Exception {
        long competitionId = 1L;
        ReviewKeyStatisticsResource reviewKeyStatisticsResource = reviewKeyStatisticsResourceBuilder.build();

        when(reviewStatisticsServiceMock.getReviewPanelKeyStatistics(competitionId)).thenReturn(serviceSuccess(reviewKeyStatisticsResource));
        mockMvc.perform(get("/competition-statistics/{id}/review", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(reviewKeyStatisticsResourceFields)
                ));
    }

    @Test
    public void getReviewInviteStatistics() throws Exception {
        long competitionId = 1L;
        ReviewInviteStatisticsResource reviewInviteStatisticsResource = reviewInviteStatisticsResourceBuilder.build();

        when(reviewStatisticsServiceMock.getReviewInviteStatistics(competitionId)).thenReturn(serviceSuccess(reviewInviteStatisticsResource));
        mockMvc.perform(get("/competition-statistics/{id}/review-invites", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition the stats are for")
                        ),
                        responseFields(reviewInviteStatisticsResourceFields)
                ));
    }

    @Test
    public void getInterviewStatistics() throws Exception {
        long competitionId = 1L;
        when(interviewStatisticsServiceMock.getInterviewPanelKeyStatistics(competitionId)).thenReturn(serviceSuccess(interviewAssignmentKeyStatisticsResourceBuilder.build()));

        mockMvc.perform(get("/competition-statistics/{id}/interview", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition")
                        ),
                        responseFields(interviewAssignmentKeyStatisticsResourceFields)
                ));

        verify(interviewStatisticsServiceMock, only()).getInterviewPanelKeyStatistics(competitionId);
    }

    @Test
    public void getInterviewInviteStatistics() throws Exception {
        long competitionId = 1L;
        when(interviewStatisticsServiceMock.getInterviewInviteStatistics(competitionId)).thenReturn(serviceSuccess(interviewInviteStatisticsResourceBuilder.build()));

        mockMvc.perform(get("/competition-statistics/{id}/interview-invites", competitionId))
                .andExpect(status().isOk())
                .andDo(document("competition-statistics/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the competition")
                        ),
                        responseFields(interviewInviteStatisticsResourceFields)
                ));

        verify(interviewStatisticsServiceMock, only()).getInterviewInviteStatistics(competitionId);
    }
}