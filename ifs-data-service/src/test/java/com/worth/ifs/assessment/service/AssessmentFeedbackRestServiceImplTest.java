package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.assessmentFeedbackResourceListType;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentFeedbackRestServiceImplTest extends BaseRestServiceUnitTest<AssessmentFeedbackRestServiceImpl> {
    private static final String assessmentFeedbackRestURL = "/assessment-feedback";

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected AssessmentFeedbackRestServiceImpl registerRestServiceUnderTest() {
        final AssessmentFeedbackRestServiceImpl assessmentFeedbackRestService = new AssessmentFeedbackRestServiceImpl();
        assessmentFeedbackRestService.setAssessmentFeedbackRestURL(assessmentFeedbackRestURL);
        return assessmentFeedbackRestService;
    }

    @Test
    public void getAllAssessmentFeedback() throws Exception {
        final List<AssessmentFeedbackResource> expected = newAssessmentFeedbackResource()
                .build(2);

        final Long assessmentId = 1L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s", assessmentFeedbackRestURL, assessmentId), assessmentFeedbackResourceListType(), expected, OK);
        final List<AssessmentFeedbackResource> response = service.getAllAssessmentFeedback(assessmentId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion() throws Exception {
        final AssessmentFeedbackResource expected = newAssessmentFeedbackResource()
                .build();

        final Long assessmentId = 1L;
        final Long questionId = 2L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s/question/%s", assessmentFeedbackRestURL, assessmentId, questionId), AssessmentFeedbackResource.class, expected, OK);
        final AssessmentFeedbackResource response = service.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void updateFeedbackValue() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final String value = "Blah";

        setupPostWithRestResultExpectations(format("%s/assessment/%s/question/%s?feedback-value=%s", assessmentFeedbackRestURL, assessmentId, questionId, value), null, OK);
        final RestResult<Void> response = service.updateFeedbackValue(assessmentId, questionId, value);
        assertTrue(response.isSuccess());
    }

    @Test
    public void updateFeedbackScore() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final Integer score = 10;

        setupPostWithRestResultExpectations(format("%s/assessment/%s/question/%s?feedback-score=%s", assessmentFeedbackRestURL, assessmentId, questionId, score), null, OK);
        final RestResult<Void> response = service.updateFeedbackScore(assessmentId, questionId, score);
        assertTrue(response.isSuccess());
    }
}