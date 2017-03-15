package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessorFormInputResponseResourceListType;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class AssessorFormInputResponseRestServiceImplTest extends BaseRestServiceUnitTest<AssessorFormInputResponseRestServiceImpl> {
    private static String assessorFormInputResponseRestUrl = "/assessment";

    @Override
    protected AssessorFormInputResponseRestServiceImpl registerRestServiceUnderTest() {
        AssessorFormInputResponseRestServiceImpl assessorFormInputResponseRestService = new AssessorFormInputResponseRestServiceImpl();
        assessorFormInputResponseRestService.setAssessorFormInputResponseRestUrl(assessorFormInputResponseRestUrl);
        return assessorFormInputResponseRestService;
    }

    @Test
    public void testGetAllAssessorFormInputResponses() throws Exception {
        List<AssessorFormInputResponseResource> expected = Arrays.asList(1,2,3).stream().map(i -> new AssessorFormInputResponseResource()).collect(Collectors.toList());

        Long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s", assessorFormInputResponseRestUrl, assessmentId), assessorFormInputResponseResourceListType(), expected, OK);
        List<AssessorFormInputResponseResource> response = service.getAllAssessorFormInputResponses(assessmentId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void testGetAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        List<AssessorFormInputResponseResource> expected = Arrays.asList(1,2,3).stream().map(i -> new AssessorFormInputResponseResource()).collect(Collectors.toList());

        Long assessmentId = 1L;
        Long questionId = 2L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s/question/%s", assessorFormInputResponseRestUrl, assessmentId, questionId), assessorFormInputResponseResourceListType(), expected, OK);
        List<AssessorFormInputResponseResource> response = service.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void testUpdateFormInputResponse() throws Exception {
        Long assessmentId = 1L;
        Long formInputId = 2L;
        String value = "Feedback";

        AssessorFormInputResponseResource formInputResponse = new AssessorFormInputResponseResource();
        formInputResponse.setAssessment(assessmentId);
        formInputResponse.setFormInput(formInputId);
        formInputResponse.setValue(value);
        setupPutWithRestResultExpectations(format("%s", assessorFormInputResponseRestUrl), formInputResponse, OK);
        final RestResult<Void> response = service.updateFormInputResponse(formInputResponse);
        assertTrue(response.isSuccess());
    }

    //     public ApplicationAssessmentAggregateResource(int totalScope, int inScope, Map<Long, BigDecimal> scores, long averagePercentage) {


    @Test
    public void getApplicationAssessmentAggregate() {
        long applicationId = 7;
        Map<Long, BigDecimal> expectedScores = new HashMap<>();
        expectedScores.put(17L, new BigDecimal(20));
        ApplicationAssessmentAggregateResource expected = new ApplicationAssessmentAggregateResource(13, 11, expectedScores, 17);

        setupGetWithRestResultExpectations(format("%s/application/%s/scores", assessorFormInputResponseRestUrl, applicationId), ApplicationAssessmentAggregateResource.class, expected, OK);
        ApplicationAssessmentAggregateResource response = service.getApplicationAssessmentAggregate(applicationId).getSuccessObjectOrThrowException();

        assertSame(expected, response);
    }

    @Test
    public void getAssessmentAggregateFeedback() {
        long applicationId = 1L;
        long questionId = 2L;

        AssessmentFeedbackAggregateResource expected = newAssessmentFeedbackAggregateResource().build();

        setupGetWithRestResultExpectations(format("%s/application/%s/question/%s/feedback", assessorFormInputResponseRestUrl, applicationId, questionId), AssessmentFeedbackAggregateResource.class, expected, OK);
        AssessmentFeedbackAggregateResource response = service.getAssessmentAggregateFeedback(applicationId, questionId).getSuccessObjectOrThrowException();

        assertSame(expected, response);
    }
}
