package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.fail;

public class AssessmentFeedbackControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentFeedbackController> {

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    @Override
    protected void setControllerUnderTest(final AssessmentFeedbackController controller) {
        this.controller = controller;
    }

    @Ignore("TODO")
    @Test
    public void getAllAssessmentFeedback() throws Exception {
        fail();
    }

    @Ignore("TODO")
    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion() throws Exception {
        fail();
    }

    @Ignore("TODO")
    @Test
    public void updateFeedbackValue() throws Exception {
        fail();
    }

    @Ignore("TODO")
    @Test
    public void updateFeedbackScore() throws Exception {
        fail();
    }
}