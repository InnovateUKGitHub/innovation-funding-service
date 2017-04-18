package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class AssessmentServiceSecurityTest extends BaseServiceSecurityTest<AssessmentService> {

    private static Long ID_TO_FIND = 1L;
    private static int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;
    private AssessmentPermissionRules assessmentPermissionRules;
    private AssessmentLookupStrategy assessmentLookupStrategy;
    private ApplicationPermissionRules applicationPermissionRules;
    private ApplicationLookupStrategy applicationLookupStrategy;

    @Override
    protected Class<? extends AssessmentService> getClassUnderTest() {
        return TestAssessmentService.class;
    }

    @Before
    public void setUp() throws Exception {
        assessmentPermissionRules = getMockPermissionRulesBean(AssessmentPermissionRules.class);
        assessmentLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessmentLookupStrategy.class);
        applicationPermissionRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);
    }

    @Test
    public void findById() {
        AssessmentResource assessmentResource = newAssessmentResource().with(id(ID_TO_FIND)).build();

        assertAccessDenied(
                () -> classUnderTest.findById(ID_TO_FIND),
                () -> verify(assessmentPermissionRules).userCanReadAssessment(eq(assessmentResource), isA(UserResource.class))
        );
    }

    @Test
    public void findAssignableById() {
        AssessmentResource assessmentResource = newAssessmentResource().with(id(ID_TO_FIND)).build();

        assertAccessDenied(
                () -> classUnderTest.findAssignableById(ID_TO_FIND),
                () -> verify(assessmentPermissionRules).userCanReadToAssign(eq(assessmentResource), isA
                        (UserResource.class))
        );
    }

    @Test
    public void findRejectableById() {
        AssessmentResource assessmentResource = newAssessmentResource().with(id(ID_TO_FIND)).build();

        assertAccessDenied(
                () -> classUnderTest.findRejectableById(ID_TO_FIND),
                () -> verify(assessmentPermissionRules).userCanReadToReject(eq(assessmentResource), isA
                        (UserResource.class))
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
    public void findByStateAndCompetition() {
        AssessmentStates state = AssessmentStates.CREATED;
        long competitionId = 1L;

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.findByStateAndCompetition(state, competitionId), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void countByStateAndCompetition() {
        AssessmentStates state = AssessmentStates.CREATED;
        long competitionId = 1L;

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.countByStateAndCompetition(state, competitionId), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getTotalScore() {
        Long assessmentId = 1L;
        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().withId(assessmentId).build());
        assertAccessDenied(
                () -> classUnderTest.getTotalScore(assessmentId),
                () -> verify(assessmentPermissionRules).userCanReadAssessmentScore(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void recommend() {
        Long assessmentId = 1L;
        AssessmentFundingDecisionOutcomeResource assessmentFundingDecisionOutcomeResource =
                newAssessmentFundingDecisionOutcomeResource().build();
        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource()
                .withId(assessmentId)
                .build());
        assertAccessDenied(
                () -> classUnderTest.recommend(assessmentId, assessmentFundingDecisionOutcomeResource),
                () -> verify(assessmentPermissionRules).userCanUpdateAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void getApplicationFeedback() throws Exception {
        long applicationId = 1L;
        ApplicationResource expectedApplicationResource = newApplicationResource()
                .withId(applicationId)
                .build();

        when(applicationLookupStrategy.getApplicationResource(applicationId)).thenReturn(expectedApplicationResource);

        assertAccessDenied(
                () -> classUnderTest.getApplicationFeedback(applicationId),
                () -> {
                    verify(applicationLookupStrategy).getApplicationResource(applicationId);
                    verify(applicationPermissionRules).usersConnectedToTheApplicationCanView(isA(ApplicationResource.class), isA(UserResource.class));
                }
        );
    }

    @Test
    public void rejectInvitation() {
        Long assessmentId = 1L;
        AssessmentRejectOutcomeResource assessmentRejectOutcomeResource = newAssessmentRejectOutcomeResource().build();
        when(assessmentLookupStrategy.getAssessmentResource(assessmentId)).thenReturn(newAssessmentResource().withId(assessmentId).build());
        assertAccessDenied(
                () -> classUnderTest.rejectInvitation(assessmentId, assessmentRejectOutcomeResource),
                () -> verify(assessmentPermissionRules).userCanUpdateAssessment(isA(AssessmentResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void withdrawAssessment() {
        Long assessmentId = 1L;
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.withdrawAssessment(assessmentId), COMP_ADMIN, PROJECT_FINANCE);
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

    @Test
    public void submitAssessments() throws Exception {
        AssessmentSubmissionsResource assessmentSubmissions = newAssessmentSubmissionsResource().withAssessmentIds(asList(1L, 2L)).build();

        assertAccessDenied(
                () -> classUnderTest.submitAssessments(assessmentSubmissions),
                () -> verify(assessmentPermissionRules).userCanSubmitAssessments(isA(AssessmentSubmissionsResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void createAssessment() throws Exception {
        AssessmentCreateResource assessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(1L)
                .withAssessorId(3L)
                .build();

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.createAssessment(assessmentCreateResource), COMP_ADMIN, PROJECT_FINANCE);
    }

    public static class TestAssessmentService implements AssessmentService {
        @Override
        public ServiceResult<AssessmentResource> findById(long id) {
            return serviceSuccess(newAssessmentResource().with(id(ID_TO_FIND)).build());
        }

        @Override
        public ServiceResult<AssessmentResource> findAssignableById(long id) {
            return serviceSuccess(newAssessmentResource().with(id(ID_TO_FIND)).build());
        }

        @Override
        public ServiceResult<AssessmentResource> findRejectableById(long id) {
            return serviceSuccess(newAssessmentResource().with(id(ID_TO_FIND)).build());
        }

        @Override
        public ServiceResult<List<AssessmentResource>> findByUserAndCompetition(long userId, long competitionId) {
            return serviceSuccess(newAssessmentResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<List<AssessmentResource>> findByStateAndCompetition(AssessmentStates state, long competitionId) {
            return serviceSuccess(newAssessmentResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<Integer> countByStateAndCompetition(AssessmentStates state, long competitionId) {
            return serviceSuccess(newAssessmentResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS).size());
        }

        @Override
        public ServiceResult<AssessmentTotalScoreResource> getTotalScore(long assessmentId) {
            return null;
        }

        @Override
        public ServiceResult<Void> recommend(@P("assessmentId") long assessmentId,
                                             AssessmentFundingDecisionOutcomeResource assessmentFundingDecision) {
            return null;
        }

        @Override
        public ServiceResult<ApplicationAssessmentFeedbackResource> getApplicationFeedback(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Void> rejectInvitation(@P("assessmentId") long assessmentId,
                                                    AssessmentRejectOutcomeResource assessmentRejectOutcomeResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> withdrawAssessment(@P("assessmentId") long assessmentId) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptInvitation(@P("assessmentId") long assessmentId) {
            return null;
        }

        @Override
        public ServiceResult<Void> submitAssessments(@P("assessmentSubmissions") AssessmentSubmissionsResource assessmentSubmissionsResource) {
            return null;
        }

        @Override
        public ServiceResult<AssessmentResource> createAssessment(AssessmentCreateResource assessmentCreateResource) {
            return null;
        }
    }
}
