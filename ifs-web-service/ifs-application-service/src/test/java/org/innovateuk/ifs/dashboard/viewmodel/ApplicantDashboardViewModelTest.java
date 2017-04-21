package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.util.TimeZoneUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;

/**
 * Testing view model {@link ApplicantDashboardViewModel}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicantDashboardViewModelTest {
    private Map<Long, Integer> applicationProgress;
    private List<ApplicationResource> applicationsInProgress;
    private List<Long> applicationsAssigned;
    private List<ApplicationResource> applicationsFinished;
    private List<ProjectResource> projectsInSetup;
    private Map<Long, CompetitionResource> competitions;
    private Map<Long, ApplicationState> applicationStates;

    private ApplicantDashboardViewModel viewModel;

    private final static Long APPLICATION_ID_IN_PROGRESS = 1L;
    private final static Long APPLICATION_ID_IS_CREATED = 2L;
    private final static Long APPLICATION_ID_IS_APPROVED = 3L;
    private final static Long APPLICATION_ID_IS_FINISH = 10L;
    private final static Long PROJECT_ID_IN_PROJECT = 5L;
    private final static Long APPLICATION_ID_IN_PROJECT = 15L;
    private final static Long COMPETITION_ID = 123L;

    @Before
    public void setup() {
        this.applicationProgress = asMap(APPLICATION_ID_IN_PROGRESS, 55);
        this.applicationsInProgress = newApplicationResource()
                .withId(APPLICATION_ID_IN_PROGRESS)
                .withCompetition(COMPETITION_ID)
                .withApplicationState(ApplicationState.OPEN)
                .build(1);
        this.applicationsAssigned = asList(APPLICATION_ID_IN_PROGRESS, APPLICATION_ID_IS_FINISH, APPLICATION_ID_IN_PROJECT);
        this.applicationsFinished = newApplicationResource()
                .withId(APPLICATION_ID_IS_FINISH)
                .withApplicationState(ApplicationState.REJECTED)
                .withCompetition(COMPETITION_ID)
                .build(1);
        this.projectsInSetup = newProjectResource()
                .withId(PROJECT_ID_IN_PROJECT)
                .withApplication(APPLICATION_ID_IN_PROJECT)
                .build(1);
        this.competitions = asMap(COMPETITION_ID, newCompetitionResource().withId(COMPETITION_ID).build());
        this.applicationStates = asMap(APPLICATION_ID_IN_PROGRESS, ApplicationState.OPEN,
                APPLICATION_ID_IN_PROJECT, ApplicationState.SUBMITTED,
                APPLICATION_ID_IS_FINISH, ApplicationState.REJECTED,
                APPLICATION_ID_IS_CREATED, ApplicationState.CREATED,
                APPLICATION_ID_IS_APPROVED, ApplicationState.APPROVED);

        viewModel = new ApplicantDashboardViewModel(applicationProgress, applicationsInProgress, applicationsAssigned,
                applicationsFinished, projectsInSetup, competitions, applicationStates);
    }

    @Test
    public void getProjectsInSetupNotEmptyTest() {
        assertTrue(viewModel.getProjectsInSetupNotEmpty());

        setupEmptyViewModel();
        assertFalse(viewModel.getProjectsInSetupNotEmpty());
    }

    @Test
    public void getApplicationsInProgressNotEmptyTest() {
        assertTrue(viewModel.getApplicationsInProgressNotEmpty());

        setupEmptyViewModel();
        assertFalse(viewModel.getApplicationsInProgressNotEmpty());
    }

    @Test
    public void getApplicationsInFinishedNotEmptyTest() {
        assertTrue(viewModel.getApplicationsInFinishedNotEmpty());

        setupEmptyViewModel();
        assertFalse(viewModel.getApplicationsInFinishedNotEmpty());
    }

    @Test
    public void getApplicationInProgressTextTest() {
        assertEquals("Application in progress", viewModel.getApplicationInProgressText());

        applicationsInProgress = newApplicationResource().build(2);
        resetViewModel();

        assertEquals("Applications in progress", viewModel.getApplicationInProgressText());
    }

    @Test
    public void applicationIsAssignedToMeTest() {
        assertTrue(viewModel.applicationIsAssignedToMe(APPLICATION_ID_IN_PROGRESS));
        assertFalse(viewModel.applicationIsAssignedToMe(1241212L));
    }

    @Test
    public void applicationIsSubmittedTest() {
        assertTrue(viewModel.applicationIsSubmitted(APPLICATION_ID_IN_PROJECT));
        assertFalse(viewModel.applicationIsSubmitted(APPLICATION_ID_IN_PROGRESS));
    }

    @Test
    public void applicationIsCreatedTest() {
        assertTrue(viewModel.applicationIsCreated(APPLICATION_ID_IS_CREATED));
        assertFalse(viewModel.applicationIsCreated(APPLICATION_ID_IN_PROGRESS));
    }

    @Test
    public void applicationIsApprovedTest() {
        assertTrue(viewModel.applicationIsApproved(APPLICATION_ID_IS_APPROVED));
        assertFalse(viewModel.applicationIsApproved(APPLICATION_ID_IN_PROGRESS));
    }

    @Test
    public void applicationIsRejectedTest() {
        assertTrue(viewModel.applicationIsRejected(APPLICATION_ID_IS_FINISH));
        assertFalse(viewModel.applicationIsRejected(APPLICATION_ID_IN_PROGRESS));
    }

    @Test
    public void applicationIsOpenTest() {
        assertTrue(viewModel.applicationIsOpen(APPLICATION_ID_IN_PROGRESS));
        assertFalse(viewModel.applicationIsOpen(APPLICATION_ID_IN_PROJECT));
    }

    @Test
    public void applicationIsCreatedOrOpenTest() {
        assertTrue(viewModel.applicationIsCreatedOrOpen(APPLICATION_ID_IS_CREATED));
        assertTrue(viewModel.applicationIsCreatedOrOpen(APPLICATION_ID_IN_PROGRESS));
        assertFalse(viewModel.applicationIsCreatedOrOpen(APPLICATION_ID_IN_PROJECT));
    }

    @Test
    public void getApplicationStatusTest() {
        assertEquals(ApplicationState.CREATED, viewModel.getApplicationState(APPLICATION_ID_IS_CREATED));
        assertEquals(ApplicationState.OPEN, viewModel.getApplicationState(APPLICATION_ID_IN_PROGRESS));
    }

    @Test
    public void getHoursLeftBeforeSubmitTest() {
        competitions = asMap(APPLICATION_ID_IN_PROGRESS, newCompetitionResource().withId(COMPETITION_ID)
                .withEndDate(ZonedDateTime.now().plusHours(4).plusMinutes(3)).build());
        resetViewModel();

        long hoursLeft = viewModel.getHoursLeftBeforeSubmit(APPLICATION_ID_IN_PROGRESS);

        assertEquals(4L, hoursLeft);
    }

    @Test
    public void isApplicationWithin24HoursTest() {
        competitions = asMap(APPLICATION_ID_IN_PROGRESS, newCompetitionResource().withId(COMPETITION_ID)
                .withEndDate(ZonedDateTime.now().plusHours(4)).build());
        resetViewModel();

        assertTrue(viewModel.isApplicationWithin24Hours(APPLICATION_ID_IN_PROGRESS));

        competitions = asMap(APPLICATION_ID_IN_PROGRESS, newCompetitionResource().withId(COMPETITION_ID)
                .withEndDate(ZonedDateTime.now().plusHours(25)).build());
        resetViewModel();

        assertFalse(viewModel.isApplicationWithin24Hours(APPLICATION_ID_IN_PROGRESS));
    }

    @Test
    public void isClosingTodayTest() {
        competitions = asMap(APPLICATION_ID_IN_PROGRESS, newCompetitionResource().withId(COMPETITION_ID)
                .withEndDate(TimeZoneUtil.toUkTimeZone(ZonedDateTime.now())).build());
        resetViewModel();

        assertTrue(viewModel.isClosingToday(APPLICATION_ID_IN_PROGRESS));

        competitions = asMap(APPLICATION_ID_IN_PROGRESS, newCompetitionResource().withId(COMPETITION_ID)
                .withEndDate(TimeZoneUtil.toUkTimeZone(ZonedDateTime.now()).plusDays(2)).build());
        resetViewModel();

        assertFalse(viewModel.isClosingToday(APPLICATION_ID_IN_PROGRESS));
    }

    private void setupEmptyViewModel() {
        viewModel = new ApplicantDashboardViewModel(null, null,
                null, null,
                null,null,null);
    }

    private void resetViewModel() {
        viewModel = new ApplicantDashboardViewModel(applicationProgress, applicationsInProgress, applicationsAssigned,
                applicationsFinished, projectsInSetup, competitions, applicationStates);
    }
}
