package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.applicant.transactional.ApplicantService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource.DashboardPreviousApplicationResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.PREVIOUS;
import static org.innovateuk.ifs.application.resource.ApplicationState.REJECTED;

/**
 * Testing {@link ApplicantService}
 */
@Rollback
@Transactional
public class ApplicationDashboardServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    @Autowired
    private ApplicationDashboardService applicationDashboardService;

    private static final DashboardPreviousApplicationResource EXAMPLE_EXPECTED_DASHBOARD_RESOURCE = new DashboardPreviousApplicationResourceBuilder()
            .withAssignedToMe(false)
            .withApplicationState(REJECTED)
            .withLeadApplicant(false)
            .withEndDate(null)
            .withDaysLeft(0)
            .withApplicationProgress(0)
            .withAssignedToInterview(false)
            .withStartDate(LocalDate.of(2015, 11, 1))
            .withTitle("Using natural gas to heat homes")
            .withApplicationId(4)
            .withCompetitionTitle("Connected digital additive manufacturing")
            .withDashboardSection(PREVIOUS)
            .build();

    @Test
    public void testGetApplicantDashboard() {
        loginSteveSmith();
        Long userId = getSteveSmith().getId();

        ServiceResult<ApplicantDashboardResource> result = applicationDashboardService.getApplicantDashboard(userId);
        ApplicantDashboardResource dashboard = result.getSuccess();

        assertEquals(0, dashboard.getInSetup().size());
        assertEquals(0, dashboard.getEuGrantTransfer().size());
        assertEquals(4, dashboard.getInProgress().size());
        assertEquals(2, dashboard.getPrevious().size());

        assertTrue(dashboard.getPrevious().contains(EXAMPLE_EXPECTED_DASHBOARD_RESOURCE));
    }

}
