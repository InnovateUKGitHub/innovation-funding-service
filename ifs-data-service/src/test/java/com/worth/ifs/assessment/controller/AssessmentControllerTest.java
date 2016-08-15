package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_RECOMMENDATION_FAILED;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.JsonMappingUtil.fromJson;
import static org.junit.Assert.assertTrue;
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
    public void recommend() throws Exception {
        Long assessmentId = 1L;
        ProcessOutcomeResource processOutcome = newProcessOutcomeResource().build();

        when(assessmentServiceMock.recommend(assessmentId, processOutcome)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(processOutcome)))
                .andExpect(status().isOk());

        verify(assessmentServiceMock, only()).recommend(assessmentId, processOutcome);
    }

    @Test
    public void recommend_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        ProcessOutcomeResource processOutcome = newProcessOutcomeResource().build();

        when(assessmentServiceMock.recommend(assessmentId, processOutcome)).thenReturn(serviceFailure(ASSESSMENT_RECOMMENDATION_FAILED));

        MvcResult result = mockMvc.perform(put("/assessment/{id}/recommend", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(processOutcome)))
                .andExpect(status().isBadRequest())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertTrue(response.is(new Error(ASSESSMENT_RECOMMENDATION_FAILED.getErrorKey(), ASSESSMENT_RECOMMENDATION_FAILED.getErrorMessage(), null)));

        verify(assessmentServiceMock, only()).recommend(assessmentId, processOutcome);
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
    public void rejectInvitation_eventNotAccepted() throws Exception {
        Long assessmentId = 1L;
        ProcessOutcomeResource processOutcome = newProcessOutcomeResource().build();

        when(assessmentServiceMock.rejectInvitation(assessmentId, processOutcome)).thenReturn(serviceFailure(ASSESSMENT_REJECTION_FAILED));

        MvcResult result = mockMvc.perform(put("/assessment/{id}/rejectInvitation", assessmentId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(processOutcome)))
                .andExpect(status().isBadRequest())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertTrue(response.is(new Error(ASSESSMENT_REJECTION_FAILED.getErrorKey(), ASSESSMENT_REJECTION_FAILED.getErrorMessage(), null)));

        verify(assessmentServiceMock, only()).rejectInvitation(assessmentId, processOutcome);
    }
}