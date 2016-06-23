package com.worth.ifs.assessment.controller;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
@Ignore("TODO")
public class AssessmentOverviewControllerTest {

    @InjectMocks
    private AssessmentOverviewController assessmentOverviewController;
}
