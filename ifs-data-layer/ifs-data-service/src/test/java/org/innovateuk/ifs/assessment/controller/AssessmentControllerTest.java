package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentFeedbackResourceBuilder.newApplicationAssessmentFeedbackResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.commons.error.CommonErrors.forbiddenError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentControllerTest extends BaseControllerMockMVCTest<AssessmentController> {

    @Mock
    private AssessmentService assessmentService;

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void findById() throws Exception {
        AssessmentResource expected = newAssessmentResource()
                .build();

        long assessmentId = 1L;

        when(assessmentService.findById(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}", assessmentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentService, only()).findById(assessmentId);
    }

    @Test
    public void findAssignableById() throws Exception {
        AssessmentResource expected = newAssessmentResource().build();

        long assessmentId = 1L;

        when(assessmentService.findAssignableById(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}/assign", assessmentId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentService, only()).findAssignableById(assessmentId);
    }

    @Test
    public void findAssignableById_withdrawn() throws Exception {
        long assessmentId = 1L;

        Error assessmentWithdrawnError = forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId));

        when(assessmentService.findAssignableById(assessmentId)).thenReturn(serviceFailure(assessmentWithdrawnError));

        mockMvc.perform(get("/assessment/{id}/assign", assessmentId))
                .andExpect(status().isForbidden())
                .andExpect(content().json(toJson(new RestErrorResponse(assessmentWithdrawnError))));

        verify(assessmentService, only()).findAssignableById(assessmentId);
    }

    @Test
    public void findRejectableById() throws Exception {
        AssessmentResource expected = newAssessmentResource().build();

        long assessmentId = 1L;

        when(assessmentService.findRejectableById(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}/rejectable", assessmentId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentService, only()).findRejectableById(assessmentId);
    }

    @Test
    public void findRejectableById_withdrawn() throws Exception {
        long assessmentId = 1L;

        Error assessmentWithdrawnError = forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId));

        when(assessmentService.findRejectableById(assessmentId)).thenReturn(serviceFailure(assessmentWithdrawnError));

        mockMvc.perform(get("/assessment/{id}/rejectable", assessmentId))
                .andExpect(status().isForbidden())
                .andExpect(content().json(toJson(new RestErrorResponse(assessmentWithdrawnError))));

        verify(assessmentService, only()).findRejectableById(assessmentId);
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        List<AssessmentResource> expected = newAssessmentResource()
                .build(2);

        long userId = 1L;
        long competitionId = 2L;

        when(assessmentService.findByUserAndCompetition(userId, competitionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentService, only()).findByUserAndCompetition(userId, competitionId);
    }

    @Test
    public void findByUserAndApplication() throws Exception {
        List<AssessmentResource> expected = newAssessmentResource()
                .build(2);

        long userId = 1L;
        long applicationId = 2L;

        when(assessmentService.findByUserAndApplication(userId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/user/{userId}/application/{applicationId}", userId, applicationId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentService, only()).findByUserAndApplication(userId, applicationId);
    }

    @Test
    public void getTotalScore() throws Exception {
        long assessmentId = 1L;

        AssessmentTotalScoreResource expected = new AssessmentTotalScoreResource(74, 200);

        when(assessmentService.getTotalScore(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}/score", assessmentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentService, only()).getTotalScore(assessmentId);
    }

    @Test
    public void recommend() throws Exception {
        long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        when(assessmentService.recommend(assessmentId, assessmentFundingDecisionOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isOk());

        verify(assessmentService, only()).recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
    }

    @Test
    public void recommend_noFundingConfirmation() throws Exception {
        Long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        Error fundingConfirmationError = fieldError("fundingConfirmation", null, "validation.assessmentFundingDecisionOutcome.fundingConfirmation.required", "");

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fundingConfirmationError))));

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void recommend_noFeedbackAndFundingConfirmationIsTrue() throws Exception {
        long assessmentId = 1L;
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withComment(comment)
                .build();

        when(assessmentService.recommend(assessmentId, assessmentFundingDecisionOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isOk());

        verify(assessmentService, only()).recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
    }

    @Test
    public void recommend_noFeedbackAndFundingConfirmationIsFalse() throws Exception {
        Long assessmentId = 1L;
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(FALSE)
                .withComment(comment)
                .build();

        Error feedbackError = fieldError("feedback", null, "validation.assessmentFundingDecisionOutcome.feedback.required", "", "fundingConfirmation", "false", "feedback");

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(feedbackError))))
                .andReturn();

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void recommend_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        String feedback = RandomStringUtils.random(5001);
        String comment = RandomStringUtils.random(5001);
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        Error feedbackError = fieldError("feedback", feedback, "validation.field.too.many.characters", "", "5000", "0");
        Error commentError = fieldError("comment", comment, "validation.field.too.many.characters", "", "5000", "0");

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(asList(feedbackError, commentError)))));

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void recommend_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(101, "feedback"));
        String comment = String.join(" ", nCopies(101, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        Error feedbackError = fieldError("feedback", feedback, "validation.field.max.word.count", "", "100");
        Error commentError = fieldError("comment", comment, "validation.field.max.word.count", "", "100");

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(asList(feedbackError, commentError)))));

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void recommend_eventNotAccepted() throws Exception {
        long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        when(assessmentService.recommend(assessmentId, assessmentFundingDecisionOutcomeResource))
                .thenReturn(serviceFailure(ASSESSMENT_RECOMMENDATION_FAILED));

        Error recommendationFailedError = new Error(ASSESSMENT_RECOMMENDATION_FAILED.getErrorKey(), null);

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(recommendationFailedError))))
                .andReturn();

        verify(assessmentService, only()).recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
    }

    @Test
    public void getApplicationFeedback() throws Exception {
        long applicationId = 1L;

        ApplicationAssessmentFeedbackResource expectedResource = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        when(assessmentService.getApplicationFeedback(applicationId)).thenReturn(serviceSuccess(expectedResource));

        mockMvc.perform(get("/assessment/application/{applicationId}/feedback", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedResource)));

        verify(assessmentService, only()).getApplicationFeedback(applicationId);
    }

    @Test
    public void rejectInvitation() throws Exception {
        long assessmentId = 1L;
        String rejectComment = String.join(" ", nCopies(100, "comment"));
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment(rejectComment)
                .build();

        when(assessmentService.rejectInvitation(assessmentId, assessmentRejectOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/reject-invitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isOk());

        verify(assessmentService, only()).rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
    }

    @Test
    public void rejectInvitation_noReason() throws Exception {
        Long assessmentId = 1L;
        String rejectComment = String.join(" ", nCopies(100, "comment"));
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectComment(rejectComment)
                .build();

        Error rejectReasonError = fieldError("rejectReason", null, "validation.assessmentRejectOutcome.rejectReason.required", "");

        mockMvc.perform(put("/assessment/{id}/reject-invitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectReasonError))));

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void rejectInvitation_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        String rejectComment = RandomStringUtils.random(5001);
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment(rejectComment)
                .build();

        Error rejectCommentError = fieldError("rejectComment", rejectComment, "validation.field.too.many.characters", "", "5000", "0");

        mockMvc.perform(put("/assessment/{id}/reject-invitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectCommentError))));

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void rejectInvitation_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        String rejectComment = String.join(" ", nCopies(101, "comment"));
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment(rejectComment)
                .build();

        Error rejectCommentError = fieldError("rejectComment", rejectComment, "validation.field.max.word.count", "", "100");

        mockMvc.perform(put("/assessment/{id}/reject-invitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectCommentError))));

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void rejectInvitation_eventNotAccepted() throws Exception {
        long assessmentId = 1L;
        String rejectComment = String.join(" ", nCopies(100, "comment"));
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment(rejectComment)
                .build();

        when(assessmentService.rejectInvitation(assessmentId, assessmentRejectOutcomeResource)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        Error rejectionFailedError = new Error(ASSESSMENT_REJECTION_FAILED.getErrorKey(), null);

        mockMvc.perform(put("/assessment/{id}/reject-invitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectionFailedError))))
                .andReturn();

        verify(assessmentService, only()).rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
    }

    @Test
    public void acceptInvitation() throws Exception {
        long assessmentId = 1L;

        when(assessmentService.acceptInvitation(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/accept-invitation", assessmentId))
                .andExpect(status().isOk());

        verify(assessmentService, only()).acceptInvitation(assessmentId);
    }

    @Test
    public void withdrawAssessment() throws Exception {
        long assessmentId = 1L;

        when(assessmentService.withdrawAssessment(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/withdraw", assessmentId))
                .andExpect(status().isOk());

        verify(assessmentService, only()).withdrawAssessment(assessmentId);
    }

    @Test
    public void unsubmitAssessment() throws Exception {
        long assessmentId = 1L;

        when(assessmentService.unsubmitAssessment(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/unsubmit", assessmentId))
                .andExpect(status().isOk());

        verify(assessmentService, only()).unsubmitAssessment(assessmentId);
    }

    @Test
    public void submitAssessments_notEmpty() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(Arrays.asList(1L, 2L))
                .build();

        assertEquals(2, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentService.submitAssessments(assessmentSubmissions)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/submit-assessments")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentSubmissions)))
                .andExpect(status().isOk());

        verify(assessmentService, only()).submitAssessments(assessmentSubmissions);
    }

    @Test
    public void submitAssessments_null() throws Exception {
        Error error = fieldError("assessmentIds", null, "validation.assessmentSubmissions.assessmentIds.required", "");

        mockMvc.perform(put("/assessment/submit-assessments")
                .contentType(APPLICATION_JSON)
                .content("{\"assessmentIds\": null}"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(error))));

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void submitAssessments_empty() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource().build();
        assertEquals(0, assessmentSubmissions.getAssessmentIds().size());

        Error error = fieldError("assessmentIds", emptyList(), "validation.assessmentSubmissions.assessmentIds.required", "");

        mockMvc.perform(put("/assessment/submit-assessments")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentSubmissions)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(error))));

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void submitAssessments_eventNotAccepted() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(Arrays.asList(100L, 200L))
                .build();

        List<Error> errorList = Arrays.asList(
                new Error(ASSESSMENT_SUBMIT_FAILED, 100L),
                new Error(ASSESSMENT_SUBMIT_FAILED, 200L)
        );

        when(assessmentService.submitAssessments(assessmentSubmissions)).thenReturn(serviceFailure(errorList));

        mockMvc.perform(put("/assessment/submit-assessments")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentSubmissions)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(errorList))));

        verify(assessmentService, only()).submitAssessments(assessmentSubmissions);
    }

    @Test
    public void create() throws Exception {
        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(1L)
                .withAssessorId(2L)
                .build();
        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(assessmentService.createAssessment(assessmentCreateResource)).thenReturn(serviceSuccess(expectedAssessmentResource));

        mockMvc.perform(post("/assessment/")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentCreateResource)))
                .andExpect(status().isCreated())
                .andExpect(content().json(toJson(expectedAssessmentResource)));

        verify(assessmentService).createAssessment(assessmentCreateResource);
    }

    @Test
    public void createBulk() throws Exception {
        List<AssessmentCreateResource> assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(1L)
                .withAssessorId(2L)
                .build(1);
        List<AssessmentResource> expectedAssessmentResource = newAssessmentResource().build(1);

        when(assessmentService.createAssessments(assessmentCreateResource)).thenReturn(serviceSuccess(expectedAssessmentResource));

        mockMvc.perform(post("/assessment/bulk")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentCreateResource)))
                .andExpect(status().isCreated())
                .andExpect(content().json(toJson(expectedAssessmentResource)));

        verify(assessmentService).createAssessments(assessmentCreateResource);
    }
    @Test
    public void create_noApplicationId() throws Exception {
        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withAssessorId(1L)
                .build();

        mockMvc.perform(post("/assessment")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentCreateResource)))
                .andExpect(status().isNotAcceptable());

        verifyNoInteractions(assessmentService);
    }

    @Test
    public void create_noAssessorId() throws Exception {
        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(1L)
                .build();

        mockMvc.perform(post("/assessment")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentCreateResource)))
                .andExpect(status().isNotAcceptable());

        verifyNoInteractions(assessmentService);
    }
}


