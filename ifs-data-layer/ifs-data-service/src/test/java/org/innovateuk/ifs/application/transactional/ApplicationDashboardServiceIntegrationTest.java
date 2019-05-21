package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseAuthenticationAwareIntegrationTest;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.applicant.transactional.ApplicantService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;
import static java.time.ZonedDateTime.of;
import static java.time.temporal.ChronoUnit.DAYS;
import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource.DashboardApplicationInProgressResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource.DashboardPreviousApplicationResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.IN_PROGRESS;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.PREVIOUS;
import static org.innovateuk.ifs.application.resource.ApplicationState.REJECTED;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;

/**
 * Testing {@link ApplicantService}
 */
@Rollback
@Transactional
public class ApplicationDashboardServiceIntegrationTest extends BaseAuthenticationAwareIntegrationTest {

    private static final long USER_ID = 1L;

    @Autowired
    private ApplicationDashboardService applicationDashboardService;

    private static final ZonedDateTime END_DATE = of(LocalDate.of(2036, 9, 9), LocalTime.of(12, 0), ZoneId.of("Europe/London"));

    @Ignore
    @Test
    public void testGetApplicantDashboard() {
        loginSteveSmith();

        ServiceResult<ApplicantDashboardResource> result = applicationDashboardService.getApplicantDashboard(USER_ID);
        ApplicantDashboardResource dashboard = result.getSuccess();

        assertEquals(0, dashboard.getInSetup().size());
        assertEquals(0, dashboard.getEuGrantTransfer().size());
        assertEquals(4, dashboard.getInProgress().size());
        assertEquals(2, dashboard.getPrevious().size());

        DashboardApplicationInProgressResource applicationInProgress = new DashboardApplicationInProgressResourceBuilder()
                .withTitle("A new innovative solution")
                .withCompetitionTitle("Connected digital additive manufacturing")
                .withAssignedToMe(false)
                .withApplicationState(SUBMITTED)
                .withLeadApplicant(true)
                .withDashboardSection(IN_PROGRESS)
                .withApplicationId(5)
                .withAssignedToInterview(false)
                .withApplicationProgress(0)
                .withDaysLeft(DAYS.between(now(), END_DATE))
                .withEndDate(END_DATE)
                .build();
        assertEquals(applicationInProgress, dashboard.getInProgress().get(0));

        DashboardPreviousApplicationResource previousApplication = new DashboardPreviousApplicationResourceBuilder()
                .withAssignedToMe(false)
                .withApplicationState(REJECTED)
                .withLeadApplicant(false)
                .withEndDate(null)
                .withDaysLeft(0)
                .withApplicationProgress(0)
                .withAssignedToInterview(false)
                .withTitle("Using natural gas to heat homes")
                .withApplicationId(4)
                .withCompetitionTitle("Connected digital additive manufacturing")
                .withDashboardSection(PREVIOUS)
                .build();
        assertEquals(previousApplication, dashboard.getPrevious().get(1));
    }

}
