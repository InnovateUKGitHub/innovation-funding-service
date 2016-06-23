package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.assessment.transactional.AssessmentFeedbackService;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Ignore;

import java.util.List;

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
        @Override
        public ServiceResult<List<AssessmentFeedbackResource>> getAllAssessmentFeedback(Long assessmentId) {
            return null;
        }

        @Override
        public ServiceResult<AssessmentFeedbackResource> getAssessmentFeedbackByAssessmentAndQuestion(Long assessmentId, Long questionId) {
            return null;
        }
    }
}