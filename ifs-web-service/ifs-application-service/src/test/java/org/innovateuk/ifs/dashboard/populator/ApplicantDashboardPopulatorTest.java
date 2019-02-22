package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testing populator {@link ApplicantDashboardPopulator}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicantDashboardPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicantDashboardPopulator populator;

    private CompetitionResource competitionResource;

    protected UserResource loggedInUser = newUserResource().withId(1L)
            .withFirstName("James")
            .withLastName("Watts")
            .withEmail("james.watts@email.co.uk")
            .withRolesGlobal(singletonList(Role.APPLICANT))
            .withUID("2aerg234-aegaeb-23aer").build();

    private final static Long APPLICATION_ID_IN_PROGRESS = 1L;
    private final static Long APPLICATION_ID_IN_FINISH = 10L;
    private final static Long APPLICATION_ID_SUBMITTED = 100L;
    private final static Long PROJECT_ID_IN_PROJECT = 5L;
    private final static Long PROJECT_ID_IN_PROJECT_WITHDRAWN = 6L;
    private final static Long APPLICATION_ID_IN_PROJECT = 15L;
    private final static Long APPLICATION_ID_IN_PROJECT_WITHDRAWN = 150L;
    private final static Long APPLICATION_ID_HORIZON_2020 = 50L;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Before
    public void setup() {
        super.setup();
        competitionResource = newCompetitionResource()
                .withId(1L)
                .with(name("Competition x"))
                .withStartDate(ZonedDateTime.now().minusDays(2))
                .withEndDate(ZonedDateTime.now().plusDays(5))
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withMinProjectDuration(1)
                .withMaxProjectDuration(36)
                .build();
        CompetitionResource compInProjectSetup = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.PROJECT_SETUP)
                .build();
        CompetitionResource horizon2020Competition = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withCompetitionTypeName("Horizon 2020")
                .build();


        List<ApplicationResource> allApplications = newApplicationResource()
                .withId(APPLICATION_ID_IN_PROGRESS, APPLICATION_ID_IN_FINISH, APPLICATION_ID_SUBMITTED, APPLICATION_ID_IN_PROJECT_WITHDRAWN, APPLICATION_ID_IN_PROJECT, APPLICATION_ID_HORIZON_2020)
                .withCompetition(competitionResource.getId(), competitionResource.getId(), compInProjectSetup.getId(), compInProjectSetup.getId(), compInProjectSetup.getId(), horizon2020Competition.getId())
                .withApplicationState(ApplicationState.OPEN, ApplicationState.REJECTED, ApplicationState.SUBMITTED, ApplicationState.APPROVED, ApplicationState.APPROVED, ApplicationState.OPEN)
                .withCompetitionStatus(CompetitionStatus.OPEN, CompetitionStatus.CLOSED, CompetitionStatus.PROJECT_SETUP, CompetitionStatus.PROJECT_SETUP, CompetitionStatus.PROJECT_SETUP, CompetitionStatus.OPEN)
                .withCompletion(BigDecimal.valueOf(50))
                .build(6);

        when(applicationRestService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(restSuccess(allApplications));

        when(projectRestService.findByUserId(loggedInUser.getId())).thenReturn(restSuccess(newProjectResource()
                .withId(PROJECT_ID_IN_PROJECT, PROJECT_ID_IN_PROJECT_WITHDRAWN)
                .withApplication(APPLICATION_ID_IN_PROJECT, APPLICATION_ID_IN_PROJECT_WITHDRAWN)
                .withProjectState(ProjectState.SETUP, ProjectState.WITHDRAWN)
                .build(2)));

        when(applicationRestService.getApplicationById(APPLICATION_ID_IN_PROJECT)).thenReturn(restSuccess(newApplicationResource()
                .withId(APPLICATION_ID_IN_PROJECT)
                .withApplicationState(ApplicationState.SUBMITTED)
                .withCompetition(competitionResource.getId()).build()));

        when(applicationRestService.getApplicationById(APPLICATION_ID_IN_PROJECT_WITHDRAWN)).thenReturn(restSuccess(newApplicationResource()
                .withId(APPLICATION_ID_IN_PROJECT_WITHDRAWN)
                .withApplicationState(ApplicationState.SUBMITTED)
                .withCompetition(competitionResource.getId()).build()));

        when(competitionRestService.getCompetitionById(compInProjectSetup.getId())).thenReturn(restSuccess(compInProjectSetup));
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(competitionRestService.getCompetitionById(horizon2020Competition.getId())).thenReturn(restSuccess(horizon2020Competition));

        when(applicationRestService.getAssignedQuestionsCount(anyLong(), anyLong())).thenReturn(restSuccess(2));

        when(userRestService.findProcessRoleByUserId(loggedInUser.getId())).thenReturn(restSuccess(newProcessRoleResource()
                .withApplication(APPLICATION_ID_IN_PROGRESS, APPLICATION_ID_IN_PROJECT, APPLICATION_ID_IN_PROJECT_WITHDRAWN, APPLICATION_ID_IN_FINISH, APPLICATION_ID_SUBMITTED, APPLICATION_ID_HORIZON_2020)
                .withRole(LEADAPPLICANT, LEADAPPLICANT, APPLICANT, APPLICANT, APPLICANT, APPLICANT)
                .build(6)));

        UserResource user = UserResourceBuilder.newUserResource().withId(loggedInUser.getId()).withRolesGlobal(singletonList(Role.APPLICANT)).build();
        when(userRestService.retrieveUserById(loggedInUser.getId())).thenReturn(restSuccess(user));
        when(interviewAssignmentRestService.isAssignedToInterview(APPLICATION_ID_SUBMITTED)).thenReturn(restSuccess(true));
        when(interviewAssignmentRestService.isAssignedToInterview(APPLICATION_ID_IN_PROGRESS)).thenReturn(restSuccess(true));

        QuestionResource applicationTeamQuestion = newQuestionResource().build();
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionResource.getId(),
                APPLICATION_TEAM)).thenReturn(restSuccess(applicationTeamQuestion));
    }

    @Test
    public void populate() {
        ApplicantDashboardViewModel viewModel = populator.populate(loggedInUser.getId(), "originQuery");

        assertFalse(viewModel.getInProgress().isEmpty());
        assertFalse(viewModel.getEuGrantTransfers().isEmpty());
        assertFalse(viewModel.getPrevious().isEmpty());
        assertFalse(viewModel.getProjects().isEmpty());

        assertEquals(1, viewModel.getInProgress().size());

        verify(applicationRestService, times(1)).getApplicationById(APPLICATION_ID_IN_PROJECT);
        assertEquals("Application in progress", viewModel.getApplicationInProgressText());
        assertEquals("Set up your project", viewModel.getProjectSetupContainerText());
    }
}
