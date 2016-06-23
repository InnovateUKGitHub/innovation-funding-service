package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import org.junit.Before;

public class AssessmentRestServiceImplTest extends BaseRestServiceUnitTest<AssessmentRestServiceImpl> {

    private static final String assessmentRestURL = "/assessment";

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected AssessmentRestServiceImpl registerRestServiceUnderTest() {
        final AssessmentRestServiceImpl assessmentRestService = new AssessmentRestServiceImpl();
        assessmentRestService.setAssessmentRestURL(assessmentRestURL);
        return assessmentRestService;
    }
}