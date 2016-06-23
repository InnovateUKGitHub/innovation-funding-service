package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import org.junit.Before;

public class AssessmentControllerTest extends BaseControllerMockMVCTest<AssessmentController> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
    }

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

}