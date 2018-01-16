package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.panel.security.AssessmentReviewLookupStrategy;
import org.innovateuk.ifs.assessment.panel.security.AssessmentReviewPermissionRules;
import org.innovateuk.ifs.assessment.transactional.AssessmentPanelService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder.newAssessmentReviewRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessmentPanelServiceSecurityTest extends BaseServiceSecurityTest<AssessmentPanelService> {

    private static final long applicationId = 1L;
    private static final long competitionId = 2L;
    private static final long userId = 3L;
    private static final long reviewId = 4L;
    private static int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;
    private AssessmentReviewPermissionRules assessmentReviewPermissionRules;
    private AssessmentReviewLookupStrategy assessmentReviewLookupStrategy;


    @Override
    protected Class<? extends AssessmentPanelService> getClassUnderTest() {
        return TestAssessmentPanelService.class;
    }

    @Before
    public void setUp() throws Exception {
        assessmentReviewPermissionRules = getMockPermissionRulesBean(AssessmentReviewPermissionRules.class);
        assessmentReviewLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessmentReviewLookupStrategy.class);
    }

    @Test
    public void assignApplicationToPanel() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.assignApplicationToPanel(applicationId), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void unAssignApplicationFromPanel() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.unassignApplicationFromPanel(applicationId), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAssessmentReviews() {
        classUnderTest.getAssessmentReviews(userId, competitionId);
        verify(assessmentReviewPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).userCanReadAssessmentReviewOnDashboard(isA(AssessmentReviewResource.class), isA(UserResource.class));
    }

    @Test
    public void getAssessmentReview() {
        when(assessmentReviewLookupStrategy.getAssessmentReviewResource(reviewId)).thenReturn(newAssessmentReviewResource().withId(reviewId).build());
        assertAccessDenied(
                () -> classUnderTest.getAssessmentReview(reviewId),
                () -> verify(assessmentReviewPermissionRules).userCanReadAssessmentReviews(isA(AssessmentReviewResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void acceptAssessmentReview() {
        when(assessmentReviewLookupStrategy.getAssessmentReviewResource(reviewId)).thenReturn(newAssessmentReviewResource().withId(reviewId).build());
        assertAccessDenied(
                () -> classUnderTest.acceptAssessmentReview(reviewId),
                () -> verify(assessmentReviewPermissionRules).userCanUpdateAssessmentReview(isA(AssessmentReviewResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void rejectAssessmentReview() {
        AssessmentReviewRejectOutcomeResource rejectOutcomeResource = newAssessmentReviewRejectOutcomeResource().build();
        when(assessmentReviewLookupStrategy.getAssessmentReviewResource(reviewId)).thenReturn(newAssessmentReviewResource().withId(reviewId).build());
        assertAccessDenied(
                () -> classUnderTest.rejectAssessmentReview(reviewId, rejectOutcomeResource),
                () -> verify(assessmentReviewPermissionRules).userCanUpdateAssessmentReview(isA(AssessmentReviewResource.class), isA(UserResource.class))
        );
    }

    public static class TestAssessmentPanelService implements AssessmentPanelService {

        @Override
        public ServiceResult<Void> assignApplicationToPanel(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Void> unassignApplicationFromPanel(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Void> createAndNotifyReviews(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<Boolean> isPendingReviewNotifications(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<List<AssessmentReviewResource>> getAssessmentReviews(long userId, long competitionId) {
            return serviceSuccess(newAssessmentReviewResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<AssessmentReviewResource> getAssessmentReview(long assessmentReviewId) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptAssessmentReview(long assessmentReviewId) {
            return null;
        }

        @Override
        public ServiceResult<Void> rejectAssessmentReview(long assessmentReviewId, AssessmentReviewRejectOutcomeResource assessmentReviewRejectOutcomeResource) {
            return null;
        }
    }
}