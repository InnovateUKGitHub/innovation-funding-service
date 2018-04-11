package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testing populator {@link ApplicantDashboardPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicantDashboardPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicantDashboardPopulator populator;

    private final static Long APPLICATION_ID_IN_PROGRESS = 1L;
    private final static Long APPLICATION_ID_IN_FINISH = 10L;
    private final static Long APPLICATION_ID_SUBMITTED = 100L;
    private final static Long PROJECT_ID_IN_PROJECT = 5L;
    private final static Long APPLICATION_ID_IN_PROJECT = 15L;

    @Before
    public void setup() {
        super.setup();
        this.setupCompetition();

        List<ApplicationResource> allApplications = newApplicationResource()
                .withId(APPLICATION_ID_IN_PROGRESS, APPLICATION_ID_IN_FINISH, APPLICATION_ID_SUBMITTED)
                .withCompetition(competitionResource.getId(), competitionResource.getId(), competitionResource.getId())
                .withApplicationState(ApplicationState.OPEN, ApplicationState.REJECTED, ApplicationState.SUBMITTED)
                .withCompetitionStatus(CompetitionStatus.OPEN, CompetitionStatus.CLOSED, CompetitionStatus.CLOSED)
                .withCompletion(BigDecimal.valueOf(50))
                .build(3);

        when(applicationRestService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(restSuccess(allApplications));

        when(projectService.findByUser(loggedInUser.getId())).thenReturn(ServiceResult.serviceSuccess(newProjectResource()
                .withId(PROJECT_ID_IN_PROJECT)
                .withApplication(APPLICATION_ID_IN_PROJECT)
                .build(1)));

        when(applicationRestService.getApplicationById(APPLICATION_ID_IN_PROJECT)).thenReturn(restSuccess(newApplicationResource()
                .withId(APPLICATION_ID_IN_PROJECT)
                .withApplicationState(ApplicationState.SUBMITTED)
                .withCompetition(competitionResource.getId()).build()));

        when(competitionRestService.getCompetitionsByUserId(loggedInUser.getId())).thenReturn(restSuccess(competitionResources));

        when(applicationRestService.getAssignedQuestionsCount(anyLong(), anyLong())).thenReturn(restSuccess(2));

        when(processRoleService.getByUserId(loggedInUser.getId())).thenReturn(newProcessRoleResource()
                .withApplication(APPLICATION_ID_IN_PROGRESS, APPLICATION_ID_IN_PROJECT, APPLICATION_ID_IN_FINISH, APPLICATION_ID_SUBMITTED)
                .withRole(LEADAPPLICANT, LEADAPPLICANT, APPLICANT, APPLICANT)
                .build(4));

        when(interviewAssignmentRestService.isAssignedToInterview(APPLICATION_ID_SUBMITTED)).thenReturn(restSuccess(true));
        when(interviewAssignmentRestService.isAssignedToInterview(APPLICATION_ID_IN_PROGRESS)).thenReturn(restSuccess(true));
    }

    @Test
    public void populate() {
        ApplicantDashboardViewModel viewModel = populator.populate(loggedInUser.getId());

        assertFalse(viewModel.getInProgress().isEmpty());
        assertFalse(viewModel.getPrevious().isEmpty());
        assertFalse(viewModel.getProjects().isEmpty());

        assertEquals(2, viewModel.getInProgress().size());

        verify(applicationRestService, times(1)).getApplicationById(APPLICATION_ID_IN_PROJECT);
        assertEquals("Applications in progress", viewModel.getApplicationInProgressText());
    }
}
