package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.assessment.transactional.AssessorFormInputResponseService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void findByAssessmentId() {
        Long assessmentId = 1L;
        Long assessorFormInputResponseId = 2L;

        when(assessorFormInputResponseLookupStrategy.getAssessorFormInputResponseResource(assessorFormInputResponseId))
                .thenReturn(newAssessorFormInputResponseResource().withId(assessorFormInputResponseId).withAssessment(assessmentId).build());
        assertAccessDenied(
                () -> service.getAllAssessorFormInputResponses(assessmentId),
                () -> verify(assessorFormInputResponsePermissionRules).userCanReadAssessorFormInputResponse(isA(AssessorFormInputResponseResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void findByAssessmentAndQuestion() {
        Long assessmentId = 1L;
        Long assessorFormInputResponseId = 2L;
        Long questionId = 3L;

        when(assessorFormInputResponseLookupStrategy.getAssessorFormInputResponseResource(assessorFormInputResponseId)).thenReturn(newAssessorFormInputResponseResource().withId(assessorFormInputResponseId).build());
        assertAccessDenied(
                () -> service.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId),
                () -> verify(assessorFormInputResponsePermissionRules).userCanReadAssessorFormInputResponse(isA(AssessorFormInputResponseResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void updateFormInputResponse() {
        Long assessorFormInputResponseId = 2L;
        AssessorFormInputResponseResource response = newAssessorFormInputResponseResource().build();

        when(assessorFormInputResponseLookupStrategy.getAssessorFormInputResponseResource(assessorFormInputResponseId)).thenReturn(newAssessorFormInputResponseResource().withId(assessorFormInputResponseId).build());
        assertAccessDenied(
                () -> service.updateFormInputResponse(response),
                () -> verify(assessorFormInputResponsePermissionRules).userCanUpdateAssessorFormInputResponse(isA(AssessorFormInputResponseResource.class), isA(UserResource.class))
        );
    }

    public static class TestAssessorFormInputResponseService implements AssessorFormInputResponseService {
        @Override
        public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId) {
            return serviceSuccess(newAssessorFormInputResponseResource().withId(2L).withAssessment(1L).build(1));
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