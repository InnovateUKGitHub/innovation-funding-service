package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseServiceUnitTest;
import org.junit.Before;
import org.mockito.Mock;

public class AssessmentServiceImplTest extends BaseServiceUnitTest<AssessmentService> {

    @Mock
    private AssessmentRestService assessmentRestService;

    @Override
    protected AssessmentService supplyServiceUnderTest() {
        return new AssessmentServiceImpl();
    }

    @Before
    public void setUp() {
        super.setUp();
    }
}