package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_RECOMMENDATION_FAILED;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentControllerTest extends BaseControllerMockMVCTest<AssessmentController> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void findById() throws Exception {
        AssessmentResource expected = newAssessmentResource()
                .build();

        Long assessmentId = 1L;

        when(assessmentServiceMock.findById(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/{id}", assessmentId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(assessmentServiceMock, only()).findById(assessmentId);
    }

    @Test
    public void findByUserAndCompetition() throws Exception {
        List<AssessmentResource> expected = newAssessmentResource()
                .build(2);

        Long userId = 1L;
        Long competitionId = 2L;

        when(assessmentServiceMock.findByUserAndCompetition(userId, competitionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(assessmentServiceMock, only()).findByUserAndCompetition(userId, competitionId);
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource()
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        when(assessmentServiceMock.recommend(assessmentId, assessmentFundingDecision)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentFundingDecision)))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).recommend(assessmentId, assessmentFundingDecision);
    }

    @Test
    public void recommend_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        String feedback = RandomStringUtils.random(5001);
        String comment = RandomStringUtils.random(5001);
        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource()
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        Error feedbackError = fieldError("feedback", feedback, "validation.field.too.many.characters", "", "5000", "0");
        Error commentError = fieldError("comment", comment, "validation.field.too.many.characters", "", "5000", "0");

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentFundingDecision)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(asList(feedbackError, commentError)))));

        verifyZeroInteractions(assessmentServiceMock);
    }

    @Test
    public void recommend_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(101, "feedback"));
        String comment = String.join(" ", nCopies(101, "comment"));
        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource()
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        Error feedbackError = fieldError("feedback", feedback, "validation.field.max.word.count", "", "100");
        Error commentError = fieldError("comment", comment, "validation.field.max.word.count", "", "100");

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentFundingDecision)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(asList(feedbackError, commentError)))));

        verifyZeroInteractions(assessmentServiceMock);
    }

    @Test
    public void recommend_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        String feedback = String.join(" ", nCopies(100, "feedback"));
        String comment = String.join(" ", nCopies(100, "comment"));
        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource()
                .withFeedback(feedback)
                .withComment(comment)
                .build();

        when(assessmentServiceMock.recommend(assessmentId, assessmentFundingDecision)).thenReturn(serviceFailure(ASSESSMENT_RECOMMENDATION_FAILED));

        Error recommendationFailedError = new Error(ASSESSMENT_RECOMMENDATION_FAILED.getErrorKey(), null);

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assessmentFundingDecision)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(recommendationFailedError))))
                .andReturn();

        verify(assessmentServiceMock, only()).recommend(assessmentId, assessmentFundingDecision);
    }

    @Test
    public void rejectInvitation() throws Exception {
        Long assessmentId = 1L;
        ProcessOutcomeResource processOutcome = newProcessOutcomeResource().build();

        when(assessmentServiceMock.rejectInvitation(assessmentId, processOutcome)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(processOutcome)))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).rejectInvitation(assessmentId, processOutcome);
    }

    @Test
    public void rejectInvitation_exceedsWordLimit() throws Exception {
        Long assessmentId = 1L;

        ProcessOutcomeResource processOutcome = newProcessOutcomeResource()
                .build();

        when(assessmentServiceMock.rejectInvitation(assessmentId, processOutcome)).thenReturn(serviceFailure(fieldError("comment", "comment", "validation.field.max.word.count", "", 100)));

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(processOutcome)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("comment", "comment", "validation.field.max.word.count", "", "100")))));
    }

    @Test
    public void rejectInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        ProcessOutcomeResource processOutcome = newProcessOutcomeResource().build();

        when(assessmentServiceMock.rejectInvitation(assessmentId, processOutcome)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        Error rejectionFailedError = new Error(ASSESSMENT_REJECTION_FAILED.getErrorKey(), null);

        mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(processOutcome)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectionFailedError))))
                .andReturn();

        verify(assessmentServiceMock, only()).rejectInvitation(assessmentId, processOutcome);
    }
}