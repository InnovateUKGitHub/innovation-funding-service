package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;

import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
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
}