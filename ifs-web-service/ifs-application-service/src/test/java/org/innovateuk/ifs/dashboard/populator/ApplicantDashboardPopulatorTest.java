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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationForEuGrantTransferResource.DashboardApplicationForEuGrantTransferResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource.DashboardApplicationInProgressResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInSetupResource.DashboardApplicationInSetupResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource.DashboardPreviousApplicationResourceBuilder;
import static org.innovateuk.ifs.application.resource.ApplicationState.APPROVED;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPENED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * Testing populator {@link ApplicantDashboardPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicantDashboardPopulatorTest extends BaseUnitTest {

    @Mock
    private ApplicantRestService applicantRestService;

    @InjectMocks
    private ApplicantDashboardPopulator populator;

    private static final ZonedDateTime TODAY = ZonedDateTime.now();
    private static final ZonedDateTime ONE_MONTH_AGO = TODAY.minusMonths(1L);
    private static final ZonedDateTime ONE_WEEK_AGO = TODAY.minusWeeks(1L);
    private static final ZonedDateTime YESTERDAY = TODAY.minusDays(1L);
    private static final ZonedDateTime TOMORROW = TODAY.plusDays(1L);
    private static final long USER_ID = 1L;

    @Test
    public void testPopulateInSetupProjects() {
        DashboardApplicationInSetupResource inSetup = new DashboardApplicationInSetupResourceBuilder()
                .withCompetitionTitle("Competition Title")
                .withProjectId(3L)
                .withProjectTitle("Project Title")
                .build();
        List<DashboardApplicationInSetupResource> inSetUp = singletonList(inSetup);
        setupDashboard(inSetUp, emptyList(), emptyList(), emptyList());

        ApplicantDashboardViewModel viewModel = populator.populate(USER_ID);

        assertEquals(1, viewModel.getProjects().size());
        InSetupDashboardRowViewModel inSetupViewModel = viewModel.getProjects().get(0);
        assertEquals("Project Title", inSetupViewModel.getTitle());
        assertEquals(3L, inSetupViewModel.getProjectId());
        assertEquals(format("/project-setup/project/%d", 3L), inSetupViewModel.getLinkUrl());
        assertEquals("Project Title", inSetupViewModel.getProjectTitle());
    }

    @Test
    public void testSortForInSetupProjects() {
        DashboardApplicationInSetupResource startsYesterday = new DashboardApplicationInSetupResourceBuilder()
                .withTargetStartDate(YESTERDAY.toLocalDate())
                .withProjectTitle("starts in past")
                .build();
        DashboardApplicationInSetupResource startsToday = new DashboardApplicationInSetupResourceBuilder()
                .withTargetStartDate(TODAY.toLocalDate())
                .withProjectTitle("starts today")
                .build();
        DashboardApplicationInSetupResource startsTomorrow = new DashboardApplicationInSetupResourceBuilder()
                .withTargetStartDate(TOMORROW.toLocalDate())
                .withProjectTitle("starts in future")
                .build();
        List<DashboardApplicationInSetupResource> inSetUp = asList(startsYesterday, startsToday, startsTomorrow);
        setupDashboard(inSetUp, emptyList(), emptyList(), emptyList());

        ApplicantDashboardViewModel viewModel = populator.populate(USER_ID);

        List<InSetupDashboardRowViewModel> result = viewModel.getProjects();
        assertEquals(3, result.size());
        assertEquals("starts in future", result.get(0).getTitle());
        assertEquals("starts today", result.get(1).getTitle());
        assertEquals("starts in past", result.get(2).getTitle());
    }

    @Test
    public void populateEuGrantTransfers() {
        DashboardApplicationForEuGrantTransferResource euGrantTransfer = new DashboardApplicationForEuGrantTransferResourceBuilder()
                .withTitle("Title")
                .withApplicationProgress(1)
                .withApplicationState(APPROVED)
                .withCompetitionTitle("Competition Title")
                .withProjectId(5L)
                .build();
        List<DashboardApplicationForEuGrantTransferResource> euGrantTransfers = singletonList(euGrantTransfer);
        setupDashboard(emptyList(), euGrantTransfers, emptyList(), emptyList());
        ApplicantDashboardViewModel viewModel = populator.populate(USER_ID);

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
    public void testSortForEuGrantTransfers() {
        DashboardApplicationForEuGrantTransferResource approvedOne = new DashboardApplicationForEuGrantTransferResourceBuilder()
                .withApplicationId(1L)
                .withApplicationState(APPROVED)
                .withTitle("approved application 1")
                .build();
        DashboardApplicationForEuGrantTransferResource openOne = new DashboardApplicationForEuGrantTransferResourceBuilder()
                .withApplicationId(2L)
                .withApplicationState(OPENED)
                .withTitle("open application 1")
                .build();
        DashboardApplicationForEuGrantTransferResource openTwo = new DashboardApplicationForEuGrantTransferResourceBuilder()
                .withApplicationId(3L)
                .withApplicationState(OPENED)
                .withTitle("open application 2")
                .build();
        DashboardApplicationForEuGrantTransferResource approvedTwo = new DashboardApplicationForEuGrantTransferResourceBuilder()
                .withApplicationId(4L)
                .withApplicationState(APPROVED)
                .withTitle("approved application 2")
                .build();
        List<DashboardApplicationForEuGrantTransferResource> euGrantTransfers = asList(approvedOne, openOne, openTwo, approvedTwo);
        setupDashboard(emptyList(), euGrantTransfers, emptyList(), emptyList());

        ApplicantDashboardViewModel viewModel = populator.populate(USER_ID);

        List<EuGrantTransferDashboardRowViewModel> result = viewModel.getEuGrantTransfers();
        assertEquals(4, result.size());
        assertEquals("approved application 1", result.get(0).getTitle());
        assertEquals("approved application 2", result.get(1).getTitle());
        assertEquals("open application 1", result.get(2).getTitle());
        assertEquals("open application 2", result.get(3).getTitle());
    }

    @Test
    public void populateInProgress() {
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
        List<DashboardApplicationInProgressResource> dashboardApplicationInProgressResources = singletonList(inProgress);
        setupDashboard(emptyList(), emptyList(), dashboardApplicationInProgressResources, emptyList());
        ApplicantDashboardViewModel viewModel = populator.populate(USER_ID);

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
    public void testSortForInProgress() {
        DashboardApplicationInProgressResource endsYesterday = new DashboardApplicationInProgressResourceBuilder()
                .withEndDate(YESTERDAY)
                .withTitle("ends in past")
                .build();
        DashboardApplicationInProgressResource startedOneMonthAgoAndEndsToday = new DashboardApplicationInProgressResourceBuilder()
                .withStartDate(ONE_MONTH_AGO.toLocalDate())
                .withEndDate(TODAY)
                .withTitle("ends today + started 1 year ago")
                .build();
        DashboardApplicationInProgressResource startedOneWeekAgoAndEndsToday = new DashboardApplicationInProgressResourceBuilder()
                .withStartDate(ONE_WEEK_AGO.toLocalDate())
                .withEndDate(TODAY)
                .withTitle("ends today + started 1 week ago")
                .build();
        DashboardApplicationInProgressResource startedTodayAndEndsToday = new DashboardApplicationInProgressResourceBuilder()
                .withStartDate(TODAY.toLocalDate())
                .withEndDate(TODAY)
                .withTitle("ends today + started today")
                .build();
        DashboardApplicationInProgressResource endsTomorrow = new DashboardApplicationInProgressResourceBuilder()
                .withEndDate(TOMORROW)
                .withTitle("ends in future")
                .build();
        List<DashboardApplicationInProgressResource> inProgress = asList(endsYesterday, startedOneMonthAgoAndEndsToday,
                startedOneWeekAgoAndEndsToday, startedTodayAndEndsToday, endsTomorrow);
        setupDashboard(emptyList(), emptyList(), inProgress, emptyList());

        ApplicantDashboardViewModel viewModel = populator.populate(USER_ID);

        List<InProgressDashboardRowViewModel> result = viewModel.getInProgress();
        assertEquals(5, result.size());
        assertEquals("ends in past", result.get(0).getTitle());
        assertEquals("ends today + started today", result.get(1).getTitle());
        assertEquals("ends today + started 1 week ago", result.get(2).getTitle());
        assertEquals("ends today + started 1 year ago", result.get(3).getTitle());
        assertEquals("ends in future", result.get(4).getTitle());
    }

    @Test
    public void populatePrevious() {
        DashboardPreviousApplicationResource previous = new DashboardPreviousApplicationResourceBuilder()
                .withTitle("Title")
                .withApplicationId(7L)
                .withApplicationProgress(50)
                .withApplicationState(OPENED)
                .withAssignedToInterview(TRUE)
                .withAssignedToMe(TRUE)
                .withDaysLeft(0)
                .withEndDate(YESTERDAY)
                .withLeadApplicant(FALSE)
                .build();
        List<DashboardPreviousApplicationResource> dashboardPreviousApplicationResources = singletonList(previous);
        setupDashboard(emptyList(), emptyList(), emptyList(), dashboardPreviousApplicationResources);
        ApplicantDashboardViewModel viewModel = populator.populate(USER_ID);

        assertEquals(1, viewModel.getPrevious().size());
        PreviousDashboardRowViewModel previousViewModel = viewModel.getPrevious().get(0);
        assertEquals(format("/application/%d/summary", 7L), previousViewModel.getLinkUrl());
        assertEquals("Title", previousViewModel.getTitle());
        assertEquals(FALSE, previousViewModel.isApproved());
        assertEquals(TRUE, previousViewModel.isCreatedOrOpen());
        assertEquals(FALSE, previousViewModel.isInformedIneligible());
        assertEquals(FALSE, previousViewModel.isRejected());
    }

    @Test
    public void testSortForPrevious() {
        DashboardPreviousApplicationResource startsYesterday = new DashboardPreviousApplicationResourceBuilder()
                .withStartDate(YESTERDAY.toLocalDate())
                .withTitle("starts in past")
                .build();
        DashboardPreviousApplicationResource startsToday = new DashboardPreviousApplicationResourceBuilder()
                .withStartDate(TODAY.toLocalDate())
                .withTitle("starts today")
                .build();
        DashboardPreviousApplicationResource startsTomorrow = new DashboardPreviousApplicationResourceBuilder()
                .withStartDate(TOMORROW.toLocalDate())
                .withTitle("starts in future")
                .build();
        List<DashboardPreviousApplicationResource> previous = asList(startsYesterday, startsToday, startsTomorrow);
        setupDashboard(emptyList(), emptyList(), emptyList(), previous);

        ApplicantDashboardViewModel viewModel = populator.populate(USER_ID);

        List<PreviousDashboardRowViewModel> result = viewModel.getPrevious();
        assertEquals(3, result.size());
        assertEquals("starts in future", result.get(0).getTitle());
        assertEquals("starts today", result.get(1).getTitle());
        assertEquals("starts in past", result.get(2).getTitle());
    }

    private void setupDashboard(List<DashboardApplicationInSetupResource> inSetup, List<DashboardApplicationForEuGrantTransferResource> euGrantTransfer, List<DashboardApplicationInProgressResource> inProgress, List<DashboardPreviousApplicationResource> previous) {
        ApplicantDashboardResource applicantDashboardResource = new ApplicantDashboardResource.ApplicantDashboardResourceBuilder()
                .withInSetup(inSetup)
                .withEuGrantTransfer(euGrantTransfer)
                .withInProgress(inProgress)
                .withPrevious(previous)
                .build();

        when(applicantRestService.getApplicantDashboard(USER_ID)).thenReturn(applicantDashboardResource);
    }
}