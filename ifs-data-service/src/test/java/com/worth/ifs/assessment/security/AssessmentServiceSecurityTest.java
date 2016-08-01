package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.transactional.AssessmentService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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

    @Test
    public void test_getAssessmentById_allowedIfGlobalCompAdminRole() {
        final Long assessmentId = 1L;
        RoleResource compAdminRole = newRoleResource().withType(COMP_ADMIN).build();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(compAdminRole)).build());
        when(assessmentLookupStrategy.getAssessment(assessmentId)).thenReturn(newAssessment().withId(assessmentId).build());
        service.findById(assessmentId);
        verify(assessmentPermissionRules).userCanReadAssessment(isA(Assessment.class), isA(UserResource.class));
    }

    @Test
    public void test_getAssessmentById_allowedIfAssessorRole() {

        RoleResource assessorRole = newRoleResource().withType(ASSESSOR).build();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(assessorRole)).build());
        service.findById(1L);
    }


    @Test
    public void testAccessIsDenied() {
        final Long assessmentId = 1L;
        RoleResource applicantRole = newRoleResource().withType(APPLICANT).build();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(applicantRole)).build());
        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().withId(assessmentId).build());
        assertAccessDenied(
                () -> service.findById(assessmentId),
                () -> verify(assessmentPermissionRules).userCanReadAssessment(isA(Assessment.class), isA(UserResource.class))
        );
    }

    public static class TestAssessmentService implements AssessmentService {
        @Override
        public ServiceResult<AssessmentResource> findById(Long id) {
           // return serviceSuccess(newAssessmentResource().withId(id).build());
            return null;
        }

        @Override
        public ServiceResult<Void> updateStatus(Long id, ProcessOutcome processOutcome) {
            return null;
        }

    }
}