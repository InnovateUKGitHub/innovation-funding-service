package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import org.junit.Before;

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

}