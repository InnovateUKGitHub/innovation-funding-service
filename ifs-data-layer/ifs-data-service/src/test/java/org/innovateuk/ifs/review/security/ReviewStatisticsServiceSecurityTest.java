package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.security.AssessmentLookupStrategy;
import org.innovateuk.ifs.assessment.security.AssessmentPermissionRules;
import org.innovateuk.ifs.assessment.transactional.AssessmentService;
import org.innovateuk.ifs.assessment.transactional.AssessmentServiceImpl;
import org.innovateuk.ifs.review.transactional.ReviewStatisticsService;
import org.innovateuk.ifs.review.transactional.ReviewStatisticsServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentSubmissionsResourceBuilder.newAssessmentSubmissionsResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class ReviewStatisticsServiceSecurityTest extends BaseServiceSecurityTest<ReviewStatisticsService> {

    @Override
    protected Class<? extends ReviewStatisticsService> getClassUnderTest() {
        return ReviewStatisticsServiceImpl.class;
    }

    @Test
    public void getAssessmentPanelKeyStatistics() {
        long competitionId = 1L;

        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAssessmentPanelKeyStatistics(competitionId),
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