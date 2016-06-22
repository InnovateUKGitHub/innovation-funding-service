package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.transactional.AssessmentFeedbackService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AssessmentFeedbackServiceSecurityTest extends BaseServiceSecurityTest<AssessmentFeedbackService> {

    private AssessmentFeedbackPermissionRules assessmentFeedbackPermissionRules;
    private AssessmentFeedbackLookupStrategy assessmentFeedbackLookupStrategy;

    @Override
    protected Class<? extends AssessmentFeedbackService> getServiceClass() {
        return TestAssessmentFeedbackService.class;
    }

    @Before
    public void setUp() throws Exception {
        assessmentFeedbackPermissionRules = getMockPermissionRulesBean(AssessmentFeedbackPermissionRules.class);
        assessmentFeedbackLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessmentFeedbackLookupStrategy.class);
    }

    public static class TestAssessmentFeedbackService implements AssessmentFeedbackService {

    }
}