package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.transactional.AssessmentService;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Ignore;

@Ignore("TODO")
public class AssessmentServiceSecurityTest extends BaseServiceSecurityTest<AssessmentService> {

    private AssessmentPermissionRules assessmentPermissionRules;
    private AssessmentLookupStrategy assessmentLookupStrategy;

    @Override
    protected Class<? extends AssessmentService> getServiceClass() {
        return TestAssessmentService.class;
    }

    @Before
    public void setUp() throws Exception {
        assessmentPermissionRules = getMockPermissionRulesBean(AssessmentPermissionRules.class);
        assessmentLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessmentLookupStrategy.class);
    }

    public static class TestAssessmentService implements AssessmentService {
        @Override
        public ServiceResult<AssessmentResource> findById(Long id) {
            return null;
        }
    }
}