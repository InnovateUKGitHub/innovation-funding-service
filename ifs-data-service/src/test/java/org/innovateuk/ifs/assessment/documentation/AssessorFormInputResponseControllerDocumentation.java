package org.innovateuk.ifs.assessment.documentation;

import org.apache.commons.lang3.RandomStringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessorFormInputResponseController;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.assessment.documentation.AssessmentAggregateScoreDocs.applicationAssessmentAggregateResourceFields;
import static org.innovateuk.ifs.assessment.documentation.AssessmentFeedbackAggregateDocs.assessmentFeedbackAggregateResourceFields;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessorFormInputResponseDocs.assessorFormInputResponseFields;
import static org.innovateuk.ifs.documentation.AssessorFormInputResponseDocs.assessorFormInputResponseResourceBuilder;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AssessorFormInputResponseControllerDocumentation extends BaseControllerMockMVCTest<AssessorFormInputResponseController> {

    @Override
    protected AssessorFormInputResponseController supplyControllerUnderTest() {
        return new AssessorFormInputResponseController();
    }

    @Test
    public void getAllAssessorFormInputResponses() throws Exception {
        Long assessmentId = 1L;
        List<AssessorFormInputResponseResource> responses = assessorFormInputResponseResourceBuilder.build(2);
        when(assessorFormInputResponseServiceMock.getAllAssessorFormInputResponses(assessmentId)).thenReturn(serviceSuccess(responses));

        mockMvc.perform(get("/assessorFormInputResponse/assessment/{assessmentId}", assessmentId))
                .andDo(document("assessorFormInputResponse/{method-name}",
                        pathParameters(
                                parameterWithName("assessmentId").description("Id of the assessment associated with responses being requested")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of responses the user is allowed to see")
                        )
                ));
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        Long assessmentId = 1L;
        Long questionId = 2L;
        List<AssessorFormInputResponseResource> responses = assessorFormInputResponseResourceBuilder.build(2);
        when(assessorFormInputResponseServiceMock.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(serviceSuccess(responses));

        mockMvc.perform(get("/assessorFormInputResponse/assessment/{assessmentId}/question/{questionId}", assessmentId, questionId))
                .andDo(document("assessorFormInputResponse/{method-name}",
                        pathParameters(
                                parameterWithName("assessmentId").description("Id of the assessment associated with responses being requested"),
                                parameterWithName("questionId").description("Id of the question associated with responses being requested")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of responses the user is allowed to see")
                        )
                ));
    }

    @Test
    public void updateAssessorFormInputResponse() throws Exception {
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
        when(assessorFormInputResponseServiceMock.updateFormInputResponse(response)).thenReturn(serviceSuccess());
        when(assessorFormInputResponseServiceMock.mapToFormInputResponse(response)).thenReturn(mappedResponse);
        when(validationUtilMock.validateResponse(mappedResponse, true)).thenReturn(bindingResult);

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("assessorFormInputResponse/{method-name}",
                        requestFields(assessorFormInputResponseFields)
                ));
    }

    @Test
    public void getApplicationAggregateScores() throws Exception {
        long applicationId = 1;
        ApplicationAssessmentAggregateResource response = new ApplicationAssessmentAggregateResource();

        when(assessorFormInputResponseServiceMock.getApplicationAggregateScores(applicationId)).thenReturn(serviceSuccess(response));

        mockMvc.perform(get("/assessorFormInputResponse/application/{applicationId}/scores", applicationId))
                .andDo(document("assessorFormInputResponse/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application associated with the aggregate scores being requested")
                        ),
                        responseFields(applicationAssessmentAggregateResourceFields)
                ));
    }

    @Test
    public void getAssessmentAggregateFeedback() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        AssessmentFeedbackAggregateResource response = newAssessmentFeedbackAggregateResource().build();

        when(assessorFormInputResponseServiceMock.getAssessmentAggregateFeedback(applicationId, questionId)).thenReturn(serviceSuccess(response));

        mockMvc.perform(get("/assessorFormInputResponse/application/{applicationId}/question/{questionId}/feedback", applicationId, questionId))
                .andDo(document("assessorFormInputResponse/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application associated with the aggregate feedback being requested"),
                                parameterWithName("questionId").description("Id of the question the feedback is for")
                        ),
                        responseFields(assessmentFeedbackAggregateResourceFields)
                ));
    }
}
