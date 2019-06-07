package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationForEuGrantTransferResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInSetupResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.EuGrantTransferDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.InProgressDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.InSetupDashboardRowViewModel;
import org.innovateuk.ifs.dashboard.viewmodel.PreviousDashboardRowViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationForEuGrantTransferResource.DashboardApplicationForEuGrantTransferResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource.DashboardApplicationInProgressResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInSetupResource.DashboardApplicationInSetupResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource.DashboardPreviousApplicationResourceBuilder;
import static org.innovateuk.ifs.application.resource.ApplicationState.APPROVED;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * Testing populator {@link ApplicantDashboardPopulator}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicantDashboardPopulatorTest extends BaseUnitTest {

    @Mock
    private ApplicantRestService applicantRestService;

    @InjectMocks
    private ApplicantDashboardPopulator populator;

    private static final ZonedDateTime TOMORROW = ZonedDateTime.now().plusDays(1);
    private static final ZonedDateTime YESTERDAY = ZonedDateTime.now().minusDays(1);

    private long userId = 1L;

    @Before
    public void setup() {
        setupDashboard();
    }

    @Test
    public void populateProjects() {
        ApplicantDashboardViewModel viewModel = populator.populate(userId, "originQuery");

        assertEquals(1, viewModel.getProjects().size());

        InSetupDashboardRowViewModel inSetupViewModel = viewModel.getProjects().get(0);

        assertEquals("Project Title", inSetupViewModel.getTitle());
        assertEquals(3L, inSetupViewModel.getProjectId());
        assertEquals(format("/project-setup/project/%d", 3L), inSetupViewModel.getLinkUrl());
        assertEquals("Project Title", inSetupViewModel.getProjectTitle());
    }

    @Test
    public void populateEuGrantTransfers() {
        ApplicantDashboardViewModel viewModel = populator.populate(userId, "originQuery");

        assertFalse(viewModel.getEuGrantTransfers().isEmpty());

        EuGrantTransferDashboardRowViewModel euGrantViewModel = viewModel.getEuGrantTransfers().get(0);

        assertEquals(format("/project-setup/project/%d", 5L), euGrantViewModel.getLinkUrl());
        assertEquals("Title", euGrantViewModel.getTitle());
        assertEquals(1, euGrantViewModel.getApplicationProgress());
        assertEquals(FALSE, euGrantViewModel.isIneligible());
        assertEquals(FALSE, euGrantViewModel.isInProgress());
        assertEquals(FALSE, euGrantViewModel.isSubmitted());
        assertEquals(TRUE, euGrantViewModel.isSuccessful());
    }

    @Test
    public void populateInProgress() {
        ApplicantDashboardViewModel viewModel = populator.populate(userId, "originQuery");

        assertEquals(1, viewModel.getInProgress().size());

        InProgressDashboardRowViewModel inProgressViewModel = viewModel.getInProgress().get(0);

        assertEquals(format("/application/%d", 6L), inProgressViewModel.getLinkUrl());
        assertEquals(format("%d%% complete", 99), inProgressViewModel.getProgressMessage());
        assertEquals("Title", inProgressViewModel.getTitle());
        assertEquals(99, inProgressViewModel.getApplicationProgress());
        assertEquals(1, inProgressViewModel.getDaysLeft());
        assertEquals(23, inProgressViewModel.getHoursLeftBeforeSubmit());
        assertEquals(FALSE, inProgressViewModel.isApplicationComplete());
        assertEquals(FALSE, inProgressViewModel.isAssignedToInterview());
        assertEquals(FALSE, inProgressViewModel.isClosingToday());
        assertEquals(TRUE, inProgressViewModel.isLeadApplicant());
        assertEquals(FALSE, inProgressViewModel.isSubmitted());
        assertEquals(TRUE, inProgressViewModel.isWithin24Hours());
    }

    @Test
    public void populatePrevious() {
        ApplicantDashboardViewModel viewModel = populator.populate(userId, "originQuery");

        assertEquals(1, viewModel.getPrevious().size());

        PreviousDashboardRowViewModel previousViewModel = viewModel.getPrevious().get(0);

        assertEquals(format("/application/%d/summary", 7L), previousViewModel.getLinkUrl());
        assertEquals("Title", previousViewModel.getTitle());
        assertEquals(FALSE, previousViewModel.isApproved());
        assertEquals(TRUE, previousViewModel.isCreatedOrOpen());
        assertEquals(FALSE, previousViewModel.isInformedIneligible());
        assertEquals(FALSE, previousViewModel.isRejected());
    }

    private void setupDashboard() {
        DashboardApplicationInSetupResource inSetup = new DashboardApplicationInSetupResourceBuilder()
                .withCompetitionTitle("Competition Title")
                .withProjectId(3L)
                .withProjectTitle("Project Title")
                .build();

        DashboardApplicationForEuGrantTransferResource euGrantTransfer = new DashboardApplicationForEuGrantTransferResourceBuilder()
                .withTitle("Title")
                .withApplicationProgress(1)
                .withApplicationState(APPROVED)
                .withCompetitionTitle("Competition Title")
                .withProjectId(5L)
                .build();

        DashboardApplicationInProgressResource inProgress = new DashboardApplicationInProgressResourceBuilder()
                .withTitle("Title")
                .withEndDate(TOMORROW)
                .withDaysLeft(1)
                .withApplicationProgress(99)
                .withAssignedToInterview(FALSE)
                .withApplicationId(6L)
                .withLeadApplicant(TRUE)
                .withApplicationState(APPROVED)
                .build();

        DashboardPreviousApplicationResource previous = new DashboardPreviousApplicationResourceBuilder()
                .withTitle("Title")
                .withApplicationId(7L)
                .withApplicationProgress(50)
                .withApplicationState(OPEN)
                .withAssignedToInterview(TRUE)
                .withAssignedToMe(TRUE)
                .withDaysLeft(0)
                .withEndDate(YESTERDAY)
                .withLeadApplicant(FALSE)
                .build();

        ApplicantDashboardResource applicantDashboardResource = new ApplicantDashboardResource.ApplicantDashboardResourceBuilder()
                .withInSetup(singletonList(inSetup))
                .withEuGrantTransfer(singletonList(euGrantTransfer))
                .withInProgress(singletonList(inProgress))
                .withPrevious(singletonList(previous))
                .build();

        when(applicantRestService.getApplicantDashboard(userId)).thenReturn(applicantDashboardResource);
    }
}