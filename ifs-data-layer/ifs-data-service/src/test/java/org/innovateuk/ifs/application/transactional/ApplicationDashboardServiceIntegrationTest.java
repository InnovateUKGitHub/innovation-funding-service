package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousRowResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousRowResource.DashboardPreviousApplicationResourceBuilder;
import static org.innovateuk.ifs.application.resource.ApplicationState.REJECTED;

/**
 * Testing {@link ApplicationDashboardService}
 */
@Rollback
@Transactional
public class ApplicationDashboardServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private ApplicationDashboardService applicationDashboardService;

    @Autowired
    private ApplicationRepository applicationRepository;

    private static final DashboardPreviousRowResource EXAMPLE_EXPECTED_DASHBOARD_RESOURCE = new DashboardPreviousApplicationResourceBuilder()
            .withAssignedToMe(false)
            .withApplicationState(REJECTED)
            .withLeadApplicant(true)
            .withEndDate(null)
            .withDaysLeft(0)
            .withApplicationProgress(0)
            .withAssignedToInterview(false)
            .withStartDate(LocalDate.of(2015, 11, 1))
            .withTitle("Using natural gas to heat homes")
            .withApplicationId(4)
            .withCompetitionTitle("Connected digital additive manufacturing")
            .withCollaborationLevelSingle(true)
            .withCompetitionCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
            .build();

    @Test
    public void testGetApplicantDashboard() {
        loginSteveSmith();
        Long userId = getSteveSmith().getId();

        Application application = applicationRepository.findById(4L).get();
        application.setManageFundingEmailDate(ZonedDateTime.now());
        application.setFundingDecision(FundingDecisionStatus.UNFUNDED);
        applicationRepository.save(application);

        ServiceResult<ApplicantDashboardResource> result = applicationDashboardService.getApplicantDashboard(userId);
        ApplicantDashboardResource dashboard = result.getSuccess();

        assertEquals(0, dashboard.getInSetup().size());
        assertEquals(0, dashboard.getEuGrantTransfer().size());
        assertEquals(5, dashboard.getInProgress().size());
        assertEquals(1, dashboard.getPrevious().size());

        assertTrue(dashboard.getPrevious().contains(EXAMPLE_EXPECTED_DASHBOARD_RESOURCE));
    }

}
