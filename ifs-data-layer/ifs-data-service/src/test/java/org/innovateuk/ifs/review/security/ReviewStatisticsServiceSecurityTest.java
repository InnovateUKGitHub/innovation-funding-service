package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.review.transactional.ReviewStatisticsService;
import org.innovateuk.ifs.review.transactional.ReviewStatisticsServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class ReviewStatisticsServiceSecurityTest extends BaseServiceSecurityTest<ReviewStatisticsService> {

    @Override
    protected Class<? extends ReviewStatisticsService> getClassUnderTest() {
        return ReviewStatisticsServiceImpl.class;
    }

    @Test
    public void getAssessmentPanelKeyStatistics() {
        long competitionId = 1L;

        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getReviewPanelKeyStatistics(competitionId),
                COMP_ADMIN,
                PROJECT_FINANCE
        );
    }

    @Test
    public void getAssessmentPanelInviteStatistics() {
        long competitionId = 1L;

        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getReviewInviteStatistics(competitionId),
                COMP_ADMIN,
                PROJECT_FINANCE
        );
    }
}