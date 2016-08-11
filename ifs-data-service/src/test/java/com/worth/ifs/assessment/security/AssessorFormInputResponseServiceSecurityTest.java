package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.transactional.AssessorFormInputResponseService;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Before;
import org.junit.Ignore;

import java.util.List;

@Ignore("TODO")
public class AssessorFormInputResponseServiceSecurityTest extends BaseServiceSecurityTest<AssessorFormInputResponseService> {

    private AssessorFormInputResponsePermissionRules assessorFormInputResponsePermissionRules;
    private AssessorFormInputResponseLookupStrategy assessorFormInputResponseLookupStrategy;

    @Override
    protected Class<? extends AssessorFormInputResponseService> getServiceClass() {
        return TestAssessorFormInputResponseService.class;
    }

    @Before
    public void setUp() throws Exception {
        assessorFormInputResponsePermissionRules = getMockPermissionRulesBean(AssessorFormInputResponsePermissionRules.class);
        assessorFormInputResponseLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessorFormInputResponseLookupStrategy.class);
    }

    public static class TestAssessorFormInputResponseService implements AssessorFormInputResponseService {
        @Override
        public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId) {
            return null;
        }

        @Override
        public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> updateFormInputResponse(AssessorFormInputResponseResource response) {
            return null;
        }
    }
}