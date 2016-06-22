package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AssessmentFeedbackControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentFeedbackController> {

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    @Override
    protected void setControllerUnderTest(final AssessmentFeedbackController controller) {
        this.controller = controller;
    }

    @Test
    public void TODO() {

    }
}