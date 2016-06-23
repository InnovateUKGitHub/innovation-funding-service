package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import org.junit.Before;
import org.mockito.Mock;

public class AssessmentFeedbackServiceImplTest extends BaseServiceUnitTest<AssessmentFeedbackService> {

    @Mock
    private AssessmentFeedbackRestService assessmentFeedbackRestService;

    @Override
    protected AssessmentFeedbackService supplyServiceUnderTest() {
        return new AssessmentFeedbackServiceImpl();
    }

    @Before
    public void setUp() {
        super.setUp();
    }
}