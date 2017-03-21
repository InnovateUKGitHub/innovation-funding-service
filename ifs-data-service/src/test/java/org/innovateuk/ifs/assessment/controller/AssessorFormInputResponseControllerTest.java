package org.innovateuk.ifs.assessment.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
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
    }

    @Test
    public void updateFormInputResponse() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = RandomStringUtils.random(5000);

        FormInput input = newFormInput().build();

        AssessorFormInputResponseResource response = newAssessorFormInputResponseResource()
                .withAssessment(assessmentId)
                .withFormInput(formInputId)
                .withValue(value)
                .build();

        FormInputResponse mappedResponse = newFormInputResponse()
                .withFormInputs(input)
                .withValue(value)
                .build();

        BindingResult bindingResult = new DataBinder(mappedResponse).getBindingResult();
        ValidationMessages expected = new ValidationMessages(bindingResult);
        when(assessorFormInputResponseServiceMock.updateFormInputResponse(response)).thenReturn(serviceSuccess(response));
        when(assessorFormInputResponseServiceMock.mapToFormInputResponse(response)).thenReturn(mappedResponse);
        when(validationUtilMock.validateResponse(mappedResponse, true)).thenReturn(bindingResult);
        when(assessorFormInputResponseServiceMock.saveUpdatedFormInputResponse(response)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(toJson(response)))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(assessorFormInputResponseServiceMock).updateFormInputResponse(response);
        verify(assessorFormInputResponseServiceMock).mapToFormInputResponse(response);
        verify(validationUtilMock).validateResponse(mappedResponse, true);
        verify(assessorFormInputResponseServiceMock).saveUpdatedFormInputResponse(response);
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

        Error sizeError = fieldError("value", value, "validation.field.too.many.characters", "", "5000", "0");

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(toJson(response)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(sizeError))))
                .andReturn();

        verify(assessorFormInputResponseServiceMock, never()).updateFormInputResponse(isA(AssessorFormInputResponseResource.class));
    }

    @Test
    public void updateFormInputResponse_exceedsWordLimit() throws Exception {
        AssessorFormInputResponseResource response = newAssessorFormInputResponseResource().build();

        when(assessorFormInputResponseServiceMock.updateFormInputResponse(response)).thenReturn(serviceFailure(fieldError("value", "response", "validation.field.max.word.count", "", 100)));

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(toJson(response)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(fieldError("value", "response", "validation.field.max.word.count", "", 100)))))
                .andReturn();

        verify(assessorFormInputResponseServiceMock).updateFormInputResponse(response);
    }

    @Test
    public void getApplicationAggregateScores() throws Exception {
        long applicationId = 7;
        ApplicationAssessmentAggregateResource expected = new ApplicationAssessmentAggregateResource(5, 3, Collections.emptyMap(),20L);

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
