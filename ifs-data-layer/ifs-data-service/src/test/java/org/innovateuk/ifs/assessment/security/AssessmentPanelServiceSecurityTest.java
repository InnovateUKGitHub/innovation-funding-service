package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.AssessmentPanelService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.security.ReviewLookupStrategy;
import org.innovateuk.ifs.review.security.ReviewPermissionRules;
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
import static org.mockito.Mockito.*;

public class AssessmentPanelServiceSecurityTest extends BaseServiceSecurityTest<AssessmentPanelService> {

    private static final long applicationId = 1L;
    private static final long competitionId = 2L;
    private static final long userId = 3L;
    private static final long reviewId = 4L;
    private static int ARRAY_SIZE_FOR_POST_FILTER_TESTS = 2;
    private ReviewPermissionRules reviewPermissionRules;
    private ReviewLookupStrategy reviewLookupStrategy;


    @Override
    protected Class<? extends AssessmentPanelService> getClassUnderTest() {
        return TestAssessmentPanelService.class;
    }

    @Before
    public void setUp() throws Exception {
        reviewPermissionRules = getMockPermissionRulesBean(ReviewPermissionRules.class);
        reviewLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ReviewLookupStrategy.class);
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
        verify(reviewPermissionRules, times(ARRAY_SIZE_FOR_POST_FILTER_TESTS)).userCanReadAssessmentReviewOnDashboard(isA(ReviewResource.class), isA(UserResource.class));
    }

    @Test
    public void getAssessmentReview() {
        when(reviewLookupStrategy.getAssessmentReviewResource(reviewId)).thenReturn(newAssessmentReviewResource().withId(reviewId).build());
        assertAccessDenied(
                () -> classUnderTest.getAssessmentReview(reviewId),
                () -> verify(reviewPermissionRules).userCanReadAssessmentReviews(isA(ReviewResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void acceptAssessmentReview() {
        when(reviewLookupStrategy.getAssessmentReviewResource(reviewId)).thenReturn(newAssessmentReviewResource().withId(reviewId).build());
        assertAccessDenied(
                () -> classUnderTest.acceptAssessmentReview(reviewId),
                () -> verify(reviewPermissionRules).userCanUpdateAssessmentReview(isA(ReviewResource.class), isA(UserResource.class))
        );
    }

    @Test
    public void rejectAssessmentReview() {
        ReviewRejectOutcomeResource rejectOutcomeResource = newAssessmentReviewRejectOutcomeResource().build();
        when(reviewLookupStrategy.getAssessmentReviewResource(reviewId)).thenReturn(newAssessmentReviewResource().withId(reviewId).build());
        assertAccessDenied(
                () -> classUnderTest.rejectAssessmentReview(reviewId, rejectOutcomeResource),
                () -> verify(reviewPermissionRules).userCanUpdateAssessmentReview(isA(ReviewResource.class), isA(UserResource.class))
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
        public ServiceResult<List<ReviewResource>> getAssessmentReviews(long userId, long competitionId) {
            return serviceSuccess(newAssessmentReviewResource().build(ARRAY_SIZE_FOR_POST_FILTER_TESTS));
        }

        @Override
        public ServiceResult<ReviewResource> getAssessmentReview(long assessmentReviewId) {
            return null;
        }

        @Override
        public ServiceResult<Void> acceptAssessmentReview(long assessmentReviewId) {
            return null;
        }

        @Override
        public ServiceResult<Void> rejectAssessmentReview(long assessmentReviewId, ReviewRejectOutcomeResource reviewRejectOutcomeResource) {
            return null;
        }
    }
}