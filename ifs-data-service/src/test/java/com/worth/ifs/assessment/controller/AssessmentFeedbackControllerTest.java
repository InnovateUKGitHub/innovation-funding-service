package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import org.junit.Before;

public class AssessmentFeedbackControllerTest extends BaseControllerMockMVCTest<AssessmentFeedbackController> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
    }

    @Override
    protected AssessmentFeedbackController supplyControllerUnderTest() {
        return new AssessmentFeedbackController();
    }
}