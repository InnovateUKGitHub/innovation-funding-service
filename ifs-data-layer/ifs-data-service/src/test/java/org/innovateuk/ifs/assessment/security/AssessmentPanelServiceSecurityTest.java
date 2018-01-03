package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.transactional.AssessmentPanelService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class AssessmentPanelServiceSecurityTest extends BaseServiceSecurityTest<AssessmentPanelService> {

    private static final long applicationId = 1L;
    private static final long competitionId = 2L;
    private static final long userId = 3L;

    @Override
    protected Class<? extends AssessmentPanelService> getClassUnderTest() {
        return TestAssessmentPanelService.class;
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
    public void getAssessmentReviews() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAssessmentReviews(userId, competitionId), COMP_ADMIN, PROJECT_FINANCE);
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
            return null;
        }
    }
}