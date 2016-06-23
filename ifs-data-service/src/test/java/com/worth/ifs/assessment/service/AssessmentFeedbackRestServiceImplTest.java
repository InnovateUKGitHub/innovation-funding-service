package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.assessmentFeedbackResourceListType;
import static java.lang.String.format;
import static org.junit.Assert.assertSame;
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
                .withId(1000L, 1001L)
                .build(2);

        final Long assessmentId = 9999L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s", assessmentFeedbackRestURL, assessmentId), assessmentFeedbackResourceListType(), expected, OK);
        final List<AssessmentFeedbackResource> response = service.getAllAssessmentFeedback(assessmentId).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion() throws Exception {
        final AssessmentFeedbackResource expected = newAssessmentFeedbackResource()
                .build();

        final Long assessmentId = 9999L;
        final Long questionId = 8888L;

        setupGetWithRestResultExpectations(format("%s/assessment/%s/question/%s", assessmentFeedbackRestURL, assessmentId, questionId), AssessmentFeedbackResource.class, expected, OK);
        final AssessmentFeedbackResource response = service.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId).getSuccessObject();
        assertSame(expected, response);
    }
}