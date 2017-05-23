package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessorFormInputResponseControllerTest extends BaseControllerMockMVCTest<AssessorFormInputResponseController> {

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
                .andExpect(content().string(toJson(expected)));

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
                .andExpect(content().string(toJson(expected)));

        verify(assessorFormInputResponseServiceMock, only()).getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId);
        verifyNoMoreInteractions(assessorFormInputResponseServiceMock);
    }

    @Test
    public void updateFormInputResponses() throws Exception {
        String value = RandomStringUtils.random(5000);

        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withAssessment(1L)
                        .withFormInput(1L, 2L)
                        .withValue(value)
                        .build(2));

        when(assessorFormInputResponseServiceMock.updateFormInputResponses(responses)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(toJson(responses)))
                .andExpect(status().isOk());

        verify(assessorFormInputResponseServiceMock, only()).updateFormInputResponses(responses);
    }

    @Test
    public void updateFormInputResponses_exceedsCharacterSizeLimit() throws Exception {
        String value = RandomStringUtils.random(5000);
        String valueInvalid = RandomStringUtils.random(5001);

        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withAssessment(1L)
                        .withFormInput(1L, 2L)
                        .withValue(value, valueInvalid)
                        .build(2));

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(toJson(responses)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("responses[1].value", valueInvalid,
                        "validation.field.too.many.characters", "", "5000", "0")))));

        verifyZeroInteractions(assessorFormInputResponseServiceMock);
    }

    @Test
    public void updateFormInputResponses_exceedsWordLimit() throws Exception {
        long formInputId = 1L;
        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource().build(2));

        when(assessorFormInputResponseServiceMock.updateFormInputResponses(responses)).thenReturn(serviceFailure(
                fieldError(String.valueOf(formInputId), "response", "validation.field.max.word.count", "", 100)));

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(toJson(responses)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError(String.valueOf(formInputId), "response",
                        "validation.field.max.word.count", "", 100)))));

        verify(assessorFormInputResponseServiceMock, only()).updateFormInputResponses(responses);
    }

    @Test
    public void getApplicationAggregateScores() throws Exception {
        long applicationId = 7;
        ApplicationAssessmentAggregateResource expected = new ApplicationAssessmentAggregateResource(true, 5, 3, Collections.emptyMap(), 20L);

        when(assessorFormInputResponseServiceMock.getApplicationAggregateScores(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessorFormInputResponse//application/{applicationId}/scores", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(assessorFormInputResponseServiceMock, only()).getApplicationAggregateScores(applicationId);
    }

    @Test
    public void getAssessmentAggregateFeedback() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        AssessmentFeedbackAggregateResource expected = newAssessmentFeedbackAggregateResource().build();

        when(assessorFormInputResponseServiceMock.getAssessmentAggregateFeedback(applicationId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessorFormInputResponse/application/{applicationId}/question/{questionId}/feedback", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(assessorFormInputResponseServiceMock, only()).getAssessmentAggregateFeedback(applicationId, questionId);
    }
}
