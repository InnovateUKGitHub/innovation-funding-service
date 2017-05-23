package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessorFormInputResponseController;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.assessment.documentation.AssessmentAggregateScoreDocs.applicationAssessmentAggregateResourceFields;
import static org.innovateuk.ifs.assessment.documentation.AssessmentFeedbackAggregateDocs.assessmentFeedbackAggregateResourceFields;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessorFormInputResponseDocs.assessorFormInputResponseResourceBuilder;
import static org.innovateuk.ifs.documentation.AssessorFormInputResponseDocs.assessorFormInputResponsesFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
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
    public void updateAssessorFormInputResponses() throws Exception {
        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .withId(1L, 2L)
                        .withAssessment(1L)
                        .withFormInput(1L, 2L)
                        .withQuestion(4L)
                        .withValue("Response 1", "Response 2")
                        .build(2));

        when(assessorFormInputResponseServiceMock.updateFormInputResponses(responses)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessorFormInputResponse")
                .contentType(APPLICATION_JSON)
                .content(toJson(responses)))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("assessorFormInputResponse/{method-name}",
                        requestFields(assessorFormInputResponsesFields)
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
