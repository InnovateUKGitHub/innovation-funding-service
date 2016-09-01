package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorFormInputResponseControllerTest extends BaseControllerMockMVCTest<AssessorFormInputResponseController> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected AssessorFormInputResponseController supplyControllerUnderTest() {
        return new AssessorFormInputResponseController();
    }

    @Test
    public void getAllAssessorFormInputResponses() throws Exception {
        List<AssessorFormInputResponseResource> expected = newAssessorFormInputResponseResource()
                .build(2);

        Long assessmentId = 1L;

        when(assessorFormInputResponseServiceMock.getAllAssessorFormInputResponses(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessorFormInputResponse/assessment/{assessmentId}", assessmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(assessorFormInputResponseServiceMock, only()).getAllAssessorFormInputResponses(assessmentId);
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        List<AssessorFormInputResponseResource> expected = newAssessorFormInputResponseResource()
                .build(2);

        Long assessmentId = 1L;
        Long questionId = 2L;

        when(assessorFormInputResponseServiceMock.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessorFormInputResponse/assessment/{assessmentId}/question/{questionId}", assessmentId, questionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(assessorFormInputResponseServiceMock, only()).getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
    }

    @Test
    public void updateFormInputResponse() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = RandomStringUtils.random(5000);

        AssessorFormInputResponseResource response = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();

        when(assessorFormInputResponseServiceMock.updateFormInputResponse(response)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk());

        verify(assessorFormInputResponseServiceMock, only()).updateFormInputResponse(response);
    }

    @Test
    public void updateFormInputResponse_exceedsCharacterSizeLimit() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = RandomStringUtils.random(5001);

        AssessorFormInputResponseResource response = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();

        Error sizeError = fieldError("value", value, "validation.assessorforminputresponse.value.length.max", "", "0", "5000");

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(sizeError))))
                .andReturn();

        verify(assessorFormInputResponseServiceMock, never()).updateFormInputResponse(isA(AssessorFormInputResponseResource.class));
    }
}