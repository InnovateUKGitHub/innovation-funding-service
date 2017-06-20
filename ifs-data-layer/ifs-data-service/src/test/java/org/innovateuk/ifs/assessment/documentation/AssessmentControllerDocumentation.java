package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessmentController;
import org.innovateuk.ifs.assessment.resource.*;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.assessment.documentation.AssessmentFundingDecisionOutcomeDocs.assessmentFundingDecisionOutcomeResourceBuilder;
import static org.innovateuk.ifs.assessment.documentation.AssessmentFundingDecisionOutcomeDocs.assessmentFundingDecisionOutcomeResourceFields;
import static org.innovateuk.ifs.assessment.documentation.AssessmentRejectOutcomeDocs.assessmentRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.assessment.documentation.AssessmentRejectOutcomeDocs.assessmentRejectOutcomeResourceFields;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessmentDocs.*;
import static org.innovateuk.ifs.documentation.AssessmentTotalScoreResourceDocs.assessmentTotalScoreResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessmentTotalScoreResourceDocs.assessmentTotalScoreResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentControllerDocumentation extends BaseControllerMockMVCTest<AssessmentController> {

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void findById() throws Exception {
        long assessmentId = 1L;
        AssessmentResource assessmentResource = assessmentResourceBuilder.build();

        when(assessmentServiceMock.findById(assessmentId)).thenReturn(serviceSuccess(assessmentResource));

        mockMvc.perform(get("/assessment/{id}", assessmentId))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the assessment that is being requested")
                        ),
                        responseFields(assessmentFields)
                ));
    }

    @Test
    public void findAssignableById() throws Exception {
        long assessmentId = 1L;
        AssessmentResource assessmentResource = assessmentResourceBuilder.build();

        when(assessmentServiceMock.findAssignableById(assessmentId)).thenReturn(serviceSuccess(assessmentResource));

        mockMvc.perform(get("/assessment/{id}/assign", assessmentId))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the assessment that is being requested")
                        ),
                        responseFields(assessmentFields)
                ));
    }

    @Test
    public void findRejectableById() throws Exception {
        long assessmentId = 1L;
        AssessmentResource assessmentResource = assessmentResourceBuilder.build();

        when(assessmentServiceMock.findRejectableById(assessmentId)).thenReturn(serviceSuccess(assessmentResource));

        mockMvc.perform(get("/assessment/{id}/rejectable", assessmentId))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the assessment that is being requested")
                        ),
                        responseFields(assessmentFields)
                ));
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        long userId = 1L;
        long competitionId = 2L;
        List<AssessmentResource> assessmentResources = assessmentResourceBuilder.build(2);

        when(assessmentServiceMock.findByUserAndCompetition(userId, competitionId)).thenReturn(serviceSuccess(assessmentResources));

        mockMvc.perform(get("/assessment/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the user whose assessments are being requested"),
                                parameterWithName("competitionId").description("Id of the competition associated with the user's assessments")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of assessments the user is allowed to see")
                        )
                ));
    }

    @Test
    public void getTotalScore() throws Exception {
        Long assessmentId = 1L;
        AssessmentTotalScoreResource assessmentTotalScoreResource = assessmentTotalScoreResourceBuilder.build();

        when(assessmentServiceMock.getTotalScore(assessmentId)).thenReturn(serviceSuccess(assessmentTotalScoreResource));

        mockMvc.perform(get("/assessment/{id}/score", assessmentId))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the assessment that is being requested")
                        ),
                        responseFields(assessmentTotalScoreResourceFields)
                ));
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 1L;
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                assessmentFundingDecisionOutcomeResourceBuilder.build();

        when(assessmentServiceMock.recommend(assessmentId, assessmentFundingDecisionOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the assessment for which to recommend")
                        ),
                        requestFields(assessmentFundingDecisionOutcomeResourceFields)
                ));
    }

    @Test
    public void getApplicationFeedback() throws Exception {
        long applicationId = 1L;

        ApplicationAssessmentFeedbackResource expectedResource = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        when(assessmentServiceMock.getApplicationFeedback(applicationId)).thenReturn(serviceSuccess(expectedResource));

        mockMvc.perform(get("/assessment/application/{id}/feedback", applicationId))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the application to retrieve assessment feedback for")
                        ),
                        responseFields(
                                fieldWithPath("feedback[]").description("List of assessor feedback items for the application")
                        )
                ));
    }

    @Test
    public void reject() throws Exception {
        Long assessmentId = 1L;
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = assessmentRejectOutcomeResourceBuilder.build();

        when(assessmentServiceMock.rejectInvitation(assessmentId, assessmentRejectOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRejectOutcomeResource)))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("id of the assessment for which to reject")
                        ),
                        requestFields(assessmentRejectOutcomeResourceFields)
                ));
    }

    @Test
    public void accept() throws Exception {
        Long assessmentId = 1L;

        when(assessmentServiceMock.acceptInvitation(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/acceptInvitation", assessmentId))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the assessment for which to accept")
                        )
                ));
    }

    @Test
    public void withdrawAssessment() throws Exception {
        Long assessmentId = 1L;

        when(assessmentServiceMock.withdrawAssessment(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/withdraw", assessmentId))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the assessment for which to withdraw")
                        )
                ));
    }


    @Test
    public void submitAssessments() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = assessmentSubmissionsResourceBuilder.build();
        when(assessmentServiceMock.submitAssessments(assessmentSubmissions)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/submitAssessments")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentSubmissions)))
                .andExpect(status().isOk())
                .andDo(document("assessment/{method-name}", requestFields(assessmentSubmissionsFields)));
    }
}
