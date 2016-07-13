package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorFormInputResponseControllerTest extends BaseControllerMockMVCTest<AssessorFormInputResponseController> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected AssessorFormInputResponseController supplyControllerUnderTest() {
        return new AssessorFormInputResponseController();
    }

    @Test
    public void testGetAllAssessorFormInputResponses() throws Exception {
        List<AssessorFormInputResponseResource> expected = newAssessorFormInputResponseResource()
                .build(2);

        Long assessmentId = 1L;

        when(assessorFormInputResponseServiceMock.getAllAssessorFormInputResponses(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessorFormInputResponse/assessment/{assessmentId}", assessmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));

        verify(assessorFormInputResponseServiceMock, only()).getAllAssessorFormInputResponses(assessmentId);
    }

    @Test
    public void testGetAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        List<AssessorFormInputResponseResource> expected = newAssessorFormInputResponseResource()
                .build(2);

        Long assessmentId = 1L;
        Long questionId = 2L;

        when(assessorFormInputResponseServiceMock.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessorFormInputResponse/assessment/{assessmentId}/question/{questionId}", assessmentId, questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));

        verify(assessorFormInputResponseServiceMock, only()).getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
    }
}