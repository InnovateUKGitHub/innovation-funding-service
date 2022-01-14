package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.controller.AssessorFormInputResponseController;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.assessment.transactional.AssessorFormInputResponseService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessorFormInputResponseDocs.assessorFormInputResponseResourceBuilder;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AssessorFormInputResponseControllerDocumentation extends BaseControllerMockMVCTest<AssessorFormInputResponseController> {

    @Mock
    private AssessorFormInputResponseService assessorFormInputResponseServiceMock;

    @Override
    protected AssessorFormInputResponseController supplyControllerUnderTest() {
        return new AssessorFormInputResponseController();
    }

    @Test
    public void getAllAssessorFormInputResponses() throws Exception {
        Long assessmentId = 1L;
        List<AssessorFormInputResponseResource> responses = assessorFormInputResponseResourceBuilder.build(2);
        when(assessorFormInputResponseServiceMock.getAllAssessorFormInputResponses(assessmentId)).thenReturn(serviceSuccess(responses));

        mockMvc.perform(get("/assessor-form-input-response/assessment/{assessmentId}", assessmentId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        Long assessmentId = 1L;
        Long questionId = 2L;
        List<AssessorFormInputResponseResource> responses = assessorFormInputResponseResourceBuilder.build(2);
        when(assessorFormInputResponseServiceMock.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(serviceSuccess(responses));

        mockMvc.perform(get("/assessor-form-input-response/assessment/{assessmentId}/question/{questionId}", assessmentId, questionId)
                .header("IFS_AUTH_TOKEN", "123abc"));
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

        mockMvc.perform(put("/assessor-form-input-response")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(responses)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getApplicationAggregateScores() throws Exception {
        long applicationId = 1;
        ApplicationAssessmentAggregateResource response = new ApplicationAssessmentAggregateResource();

        when(assessorFormInputResponseServiceMock.getApplicationAggregateScores(applicationId)).thenReturn(serviceSuccess(response));

        mockMvc.perform(get("/assessor-form-input-response/application/{applicationId}/scores", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void getAssessmentAggregateFeedback() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;
        AssessmentFeedbackAggregateResource response = newAssessmentFeedbackAggregateResource().build();

        when(assessorFormInputResponseServiceMock.getAssessmentAggregateFeedback(applicationId, questionId)).thenReturn(serviceSuccess(response));

        mockMvc.perform(get("/assessor-form-input-response/application/{applicationId}/question/{questionId}/feedback", applicationId, questionId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }
}
