package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponsesResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestServiceImpl.assessorFormInputResponseRestUrl;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessorFormInputResponseResourceListType;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class AssessorFormInputResponseRestServiceImplTest extends BaseRestServiceUnitTest<AssessorFormInputResponseRestServiceImpl> {

    @Override
    protected AssessorFormInputResponseRestServiceImpl registerRestServiceUnderTest() {
        return new AssessorFormInputResponseRestServiceImpl();
    }

    @Test
    public void getAllAssessorFormInputResponses() throws Exception {
        List<AssessorFormInputResponseResource> expected = Stream.of(1, 2, 3).map(i -> new AssessorFormInputResponseResource()).collect(Collectors.toList());

        long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s", assessorFormInputResponseRestUrl, assessmentId), assessorFormInputResponseResourceListType(), expected, OK);
        List<AssessorFormInputResponseResource> response = service.getAllAssessorFormInputResponses(assessmentId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void getAllAssessorFormInputResponsesByAssessmentAndQuestion() throws Exception {
        List<AssessorFormInputResponseResource> expected = Stream.of(1, 2, 3).map(i -> new AssessorFormInputResponseResource()).collect(Collectors.toList());

        long assessmentId = 1L;
        long questionId = 2L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s/question/%s", assessorFormInputResponseRestUrl, assessmentId, questionId), assessorFormInputResponseResourceListType(), expected, OK);
        List<AssessorFormInputResponseResource> response = service.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void updateFormInputResponse() throws Exception {
        long assessmentId = 1L;
        long formInputId = 2L;
        String value = "Response";

        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(assessmentId)
                        .withFormInput(formInputId)
                        .withValue(value)
                        .build());

        setupPutWithRestResultExpectations(format("%s", assessorFormInputResponseRestUrl), responses, OK);
        RestResult<Void> response = service.updateFormInputResponse(assessmentId, formInputId, value);
        assertTrue(response.isSuccess());
    }

    @Test
    public void updateFormInputResponses() throws Exception {
        AssessorFormInputResponsesResource responses = new AssessorFormInputResponsesResource(
                newAssessorFormInputResponseResource()
                        .with(id(null))
                        .withAssessment(1L)
                        .withFormInput(2L, 3L)
                        .withValue("Response 1", "Response 2")
                        .build(2));

        setupPutWithRestResultExpectations(format("%s", assessorFormInputResponseRestUrl), responses, OK);
        RestResult<Void> response = service.updateFormInputResponses(responses);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getApplicationAssessmentAggregate() {
        long applicationId = 7;
        Map<Long, BigDecimal> expectedScores = new HashMap<>();
        expectedScores.put(17L, new BigDecimal(20));
        ApplicationAssessmentAggregateResource expected = new ApplicationAssessmentAggregateResource(true, 13, 11, expectedScores, 17);

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
