package com.worth.ifs.assessment.security;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.transactional.AssessmentService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.List;

import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;


public class AssessmentServiceSecurityTest extends BaseServiceSecurityTest<AssessmentService> {

    private AssessmentPermissionRules assessmentPermissionRules;
    private AssessmentLookupStrategy assessmentLookupStrategy;

    @Override
    protected Class<? extends AssessmentService> getClassUnderTest() {
        return TestAssessmentService.class;
    }

    @Before
    public void setUp() throws Exception {
        assessmentPermissionRules = getMockPermissionRulesBean(AssessmentPermissionRules.class);
        assessmentLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessmentLookupStrategy.class);
    }

    private static Long ID_TO_FIND = 1L;
    private static int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;

    @Test
    public void findById() {
        AssessmentResource assessmentResource = newAssessmentResource().with(id(ID_TO_FIND)).build();

        assertAccessDenied(
                () -> classUnderTest.findById(ID_TO_FIND),
                () -> verify(assessmentPermissionRules).userCanReadAssessment(eq(assessmentResource), isA(UserResource.class))
        );
    }

    @Test
    public void findByUserAndCompetition() {
        long userId = 3L;
        long competitionId = 1L;

        classUnderTest.findByUserAndCompetition(userId, competitionId);
        verify(assessmentPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).userCanReadAssessmentOnDashboard(isA(AssessmentResource.class), isA(UserResource.class));
    }

    @Test
    public void recommend() {
        Long assessmentId = 1L;
        AssessmentFundingDecisionResource assessmentFundingDecision = newAssessmentFundingDecisionResource().build();
        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().withId(assessmentId).build());
        assertAccessDenied(
                () -> classUnderTest.recommend(assessmentId, assessmentFundingDecision),
                () -> verify(assessmentPermissionRules).userCanUpdateAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void rejectInvitation() {
        Long assessmentId = 1L;
        ApplicationRejectionResource applicationRejection = newApplicationRejectionResource().build();
        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().withId(assessmentId).build());
        assertAccessDenied(
                () -> classUnderTest.rejectInvitation(assessmentId, applicationRejection),
                () -> verify(assessmentPermissionRules).userCanUpdateAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void accept() {
        Long assessmentId = 1L;
        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().withId(assessmentId).build());
        assertAccessDenied(
                () -> classUnderTest.acceptInvitation(assessmentId),
                () -> verify(assessmentPermissionRules).userCanUpdateAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    public static class TestAssessmentService implements AssessmentService {
        @Override
        public ServiceResult<AssessmentResource> findById(Long id) {
            return serviceSuccess(newAssessmentResource().with(id(ID_TO_FIND)).build());
        }

        @Override
        public ServiceResult<List<AssessmentResource>> findByUserAndCompetition(Long userId, Long competitionId) {
            return serviceSuccess(newAssessmentResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<Void> recommend(@P("assessmentId") Long assessmentId, AssessmentFundingDecisionResource assessmentFundingDecision) {
            return null;
        }

        @Override
        public ServiceResult<Void> rejectInvitation(@P("assessmentId") Long assessmentId, ApplicationRejectionResource applicationRejection) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptInvitation(@P("assessmentId") Long assessmentId) {
            return null;
        }
    }
}