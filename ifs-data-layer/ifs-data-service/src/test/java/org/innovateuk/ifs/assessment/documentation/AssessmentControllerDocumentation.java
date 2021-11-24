package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessmentController;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.assessment.documentation.AssessmentFundingDecisionOutcomeDocs.assessmentFundingDecisionOutcomeResourceBuilder;
import static org.innovateuk.ifs.assessment.documentation.AssessmentRejectOutcomeDocs.assessmentRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessmentDocs.assessmentResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessmentDocs.assessmentSubmissionsResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessmentTotalScoreResourceDocs.assessmentTotalScoreResourceBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentControllerDocumentation extends BaseControllerMockMVCTest<AssessmentController> {

    @Mock
    private AssessmentService assessmentService;

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void findById() throws Exception {
        long assessmentId = 1L;
        AssessmentResource assessmentResource = assessmentResourceBuilder.build();

        when(assessmentService.findById(assessmentId)).thenReturn(serviceSuccess(assessmentResource));

        mockMvc.perform(get("/assessment/{id}", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void findAssignableById() throws Exception {
        long assessmentId = 1L;
        AssessmentResource assessmentResource = assessmentResourceBuilder.build();

        when(assessmentService.findAssignableById(assessmentId)).thenReturn(serviceSuccess(assessmentResource));

        mockMvc.perform(get("/assessment/{id}/assign", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void findRejectableById() throws Exception {
        long assessmentId = 1L;
        AssessmentResource assessmentResource = assessmentResourceBuilder.build();

        when(assessmentService.findRejectableById(assessmentId)).thenReturn(serviceSuccess(assessmentResource));

        mockMvc.perform(get("/assessment/{id}/rejectable", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        long userId = 1L;
        long competitionId = 2L;
        List<AssessmentResource> assessmentResources = assessmentResourceBuilder.build(2);

        when(assessmentService.findByUserAndCompetition(userId, competitionId)).thenReturn(serviceSuccess(assessmentResources));

        mockMvc.perform(get("/assessment/user/{userId}/competition/{competitionId}", userId, competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void findByUserAndApplication() throws Exception {
        long userId = 1L;
        long applicationId = 2L;
        List<AssessmentResource> assessmentResources = assessmentResourceBuilder.build(2);

        when(assessmentService.findByUserAndApplication(userId, applicationId)).thenReturn(serviceSuccess(assessmentResources));

        mockMvc.perform(get("/assessment/user/{userId}/application/{applicationId}", userId, applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }


    @Ignore
    @Test
    public void getTotalScore() throws Exception {
        long assessmentId = 1L;
        AssessmentTotalScoreResource assessmentTotalScoreResource = assessmentTotalScoreResourceBuilder.build();

        when(assessmentService.getTotalScore(assessmentId)).thenReturn(serviceSuccess(assessmentTotalScoreResource));

        mockMvc.perform(get("/assessment/{id}/score", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void recommend() throws Exception {
        long assessmentId = 1L;
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                assessmentFundingDecisionOutcomeResourceBuilder.build();

        when(assessmentService.recommend(assessmentId, assessmentFundingDecisionOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isOk());
    }

    @Test
    public void getApplicationFeedback() throws Exception {
        long applicationId = 1L;

        ApplicationAssessmentFeedbackResource expectedResource = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        when(assessmentService.getApplicationFeedback(applicationId)).thenReturn(serviceSuccess(expectedResource));

        mockMvc.perform(get("/assessment/application/{id}/feedback", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void reject() throws Exception {
        long assessmentId = 1L;
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = assessmentRejectOutcomeResourceBuilder.build();

        when(assessmentService.rejectInvitation(assessmentId, assessmentRejectOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/reject-invitation", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentRejectOutcomeResource)))
                .andExpect(status().isOk());
    }

    @Test
    public void accept() throws Exception {
        long assessmentId = 1L;

        when(assessmentService.acceptInvitation(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/accept-invitation", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void withdrawAssessment() throws Exception {
        long assessmentId = 1L;

        when(assessmentService.withdrawAssessment(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/withdraw", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void unsubmitAssessment() throws Exception {
        long assessmentId = 1L;

        when(assessmentService.unsubmitAssessment(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/unsubmit", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }


    @Test
    public void submitAssessments() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = assessmentSubmissionsResourceBuilder.build();
        when(assessmentService.submitAssessments(assessmentSubmissions)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/submit-assessments")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentSubmissions)))
                .andExpect(status().isOk());
    }
}
