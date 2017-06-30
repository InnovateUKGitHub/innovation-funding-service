package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static java.util.Collections.singletonList;
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

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void findById() throws Exception {
        AssessmentResource expected = newAssessmentResource()
                .build();

        long assessmentId = 1L;

        when(assessmentServiceMock.findById(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}", assessmentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentServiceMock, only()).findById(assessmentId);
    }

    @Test
    public void findAssignableById() throws Exception {
        AssessmentResource expected = newAssessmentResource().build();

        long assessmentId = 1L;

        when(assessmentServiceMock.findAssignableById(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}/assign", assessmentId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentServiceMock, only()).findAssignableById(assessmentId);
    }

    @Test
    public void findAssignableById_withdrawn() throws Exception {
        long assessmentId = 1L;

        Error assessmentWithdrawnError = forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId));

        when(assessmentServiceMock.findAssignableById(assessmentId)).thenReturn(serviceFailure(assessmentWithdrawnError));

        mockMvc.perform(get("/assessment/{id}/assign", assessmentId))
                .andExpect(status().isForbidden())
                .andExpect(content().json(toJson(new RestErrorResponse(assessmentWithdrawnError))));

        verify(assessmentServiceMock, only()).findAssignableById(assessmentId);
    }

    @Test
    public void findRejectableById() throws Exception {
        AssessmentResource expected = newAssessmentResource().build();

        long assessmentId = 1L;

        when(assessmentServiceMock.findRejectableById(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}/rejectable", assessmentId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentServiceMock, only()).findRejectableById(assessmentId);
    }

    @Test
    public void findRejectableById_withdrawn() throws Exception {
        long assessmentId = 1L;

        Error assessmentWithdrawnError = forbiddenError(ASSESSMENT_WITHDRAWN, singletonList(assessmentId));

        when(assessmentServiceMock.findRejectableById(assessmentId)).thenReturn(serviceFailure(assessmentWithdrawnError));

        mockMvc.perform(get("/assessment/{id}/rejectable", assessmentId))
                .andExpect(status().isForbidden())
                .andExpect(content().json(toJson(new RestErrorResponse(assessmentWithdrawnError))));

        verify(assessmentServiceMock, only()).findRejectableById(assessmentId);
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        List<AssessmentResource> expected = newAssessmentResource()
                .build(2);

        Long userId = 1L;
        Long competitionId = 2L;

        when(assessmentServiceMock.findByUserAndCompetition(userId, competitionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentServiceMock, only()).findByUserAndCompetition(userId, competitionId);
    }

    @Test
    public void getTotalScore() throws Exception {
        Long assessmentId = 1L;

        AssessmentTotalScoreResource expected = new AssessmentTotalScoreResource(74, 200);

        when(assessmentServiceMock.getTotalScore(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}/score", assessmentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(assessmentServiceMock, only()).getTotalScore(assessmentId);
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        when(assessmentServiceMock.recommend(assessmentId, assessmentFundingDecisionOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
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

        verifyZeroInteractions(assessmentServiceMock);
    }

    @Test
    public void recommend_noFeedbackAndFundingConfirmationIsTrue() throws Exception {
        Long assessmentId = 1L;
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withComment(comment)
                .build();

        when(assessmentServiceMock.recommend(assessmentId, assessmentFundingDecisionOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
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

        verifyZeroInteractions(assessmentServiceMock);
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

        verifyZeroInteractions(assessmentServiceMock);
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

        verifyZeroInteractions(assessmentServiceMock);
    }

    @Test
    public void recommend_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource()
                .withFundingConfirmation(TRUE)
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        when(assessmentServiceMock.recommend(assessmentId, assessmentFundingDecisionOutcomeResource))
                .thenReturn(serviceFailure(ASSESSMENT_RECOMMENDATION_FAILED));

        Error recommendationFailedError = new Error(ASSESSMENT_RECOMMENDATION_FAILED.getErrorKey(), null);

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentFundingDecisionOutcomeResource)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(recommendationFailedError))))
                .andReturn();

        verify(assessmentServiceMock, only()).recommend(assessmentId, assessmentFundingDecisionOutcomeResource);
    }

    @Test
    public void getApplicationFeedback() throws Exception {
        long applicationId = 1L;

        ApplicationAssessmentFeedbackResource expectedResource = newApplicationAssessmentFeedbackResource()
                .withFeedback(asList("Feedback 1", "Feedback 2"))
                .build();

        when(assessmentServiceMock.getApplicationFeedback(applicationId)).thenReturn(serviceSuccess(expectedResource));

        mockMvc.perform(get("/assessment/application/{applicationId}/feedback", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedResource)));

        verify(assessmentServiceMock, only()).getApplicationFeedback(applicationId);
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;
        String rejectComment = String.join(" ", nCopies(100, "comment"));
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment(rejectComment)
                .build();

        when(assessmentServiceMock.rejectInvitation(assessmentId, assessmentRejectOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
    }

    @Test
    public void rejectInvitation_noReason() throws Exception {
        Long assessmentId = 1L;
        String rejectComment = String.join(" ", nCopies(100, "comment"));
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectComment(rejectComment)
                .build();

        Error rejectReasonError = fieldError("rejectReason", null, "validation.assessmentRejectOutcome.rejectReason.required", "");

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectReasonError))));

        verifyZeroInteractions(assessmentServiceMock);
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

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectCommentError))));

        verifyZeroInteractions(assessmentServiceMock);
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

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectCommentError))));

        verifyZeroInteractions(assessmentServiceMock);
    }

    @Test
    public void rejectInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        String rejectComment = String.join(" ", nCopies(100, "comment"));
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment(rejectComment)
                .build();

        when(assessmentServiceMock.rejectInvitation(assessmentId, assessmentRejectOutcomeResource)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        Error rejectionFailedError = new Error(ASSESSMENT_REJECTION_FAILED.getErrorKey(), null);

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentRejectOutcomeResource)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectionFailedError))))
                .andReturn();

        verify(assessmentServiceMock, only()).rejectInvitation(assessmentId, assessmentRejectOutcomeResource);
    }

    @Test
    public void acceptInvitation() throws Exception {
        Long assessmentId = 1L;

        when(assessmentServiceMock.acceptInvitation(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/acceptInvitation", assessmentId))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).acceptInvitation(assessmentId);
    }

    @Test
    public void withdrawAssessment() throws Exception {
        Long assessmentId = 1L;

        when(assessmentServiceMock.withdrawAssessment(assessmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/withdraw", assessmentId))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).withdrawAssessment(assessmentId);
    }

    @Test
    public void submitAssessments_notEmpty() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource()
                .withAssessmentIds(Arrays.asList(1L, 2L))
                .build();

        assertEquals(2, assessmentSubmissions.getAssessmentIds().size());

        when(assessmentServiceMock.submitAssessments(assessmentSubmissions)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/submitAssessments")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentSubmissions)))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).submitAssessments(assessmentSubmissions);
    }

    @Test
    public void submitAssessments_null() throws Exception {
        Error error = fieldError("assessmentIds", null, "validation.assessmentSubmissions.assessmentIds.required", "");

        mockMvc.perform(put("/assessment/submitAssessments")
                .contentType(APPLICATION_JSON)
                .content("{\"assessmentIds\": null}"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(error))));

        verifyZeroInteractions(assessmentServiceMock);
    }

    @Test
    public void submitAssessments_empty() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource().build();
        assertEquals(0, assessmentSubmissions.getAssessmentIds().size());

        Error error = fieldError("assessmentIds", emptyList(), "validation.assessmentSubmissions.assessmentIds.required", "");

        mockMvc.perform(put("/assessment/submitAssessments")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentSubmissions)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(error))));

        verifyZeroInteractions(assessmentServiceMock);
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

        when(assessmentServiceMock.submitAssessments(assessmentSubmissions)).thenReturn(serviceFailure(errorList));

        mockMvc.perform(put("/assessment/submitAssessments")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentSubmissions)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(errorList))));

        verify(assessmentServiceMock, only()).submitAssessments(assessmentSubmissions);
    }

    @Test
    public void create() throws Exception {
        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(1L)
                .withAssessorId(2L)
                .build();
        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();

        when(assessmentServiceMock.createAssessment(assessmentCreateResource)).thenReturn(serviceSuccess(expectedAssessmentResource));

        mockMvc.perform(post("/assessment/")
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentCreateResource)))
                .andExpect(status().isCreated())
                .andExpect(content().json(toJson(expectedAssessmentResource)));

        verify(assessmentServiceMock).createAssessment(assessmentCreateResource);
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

        verifyZeroInteractions(assessmentServiceMock);
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

        verifyZeroInteractions(assessmentServiceMock);
    }
}


