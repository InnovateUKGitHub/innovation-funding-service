package com.worth.ifs.assessment.security;

import com.worth.ifs.BasePermissionRulesTest;
import org.junit.Before;

public class AssessmentFeedbackPermissionRulesTest extends BasePermissionRulesTest<AssessmentFeedbackPermissionRules> {

    @Before
    public void setUp() throws Exception {

    }

    @Override
    protected AssessmentFeedbackPermissionRules supplyPermissionRulesUnderTest() {
        return new AssessmentFeedbackPermissionRules();
    }
}