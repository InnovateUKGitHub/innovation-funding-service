package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.user.resource.UserRoleType;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Testing populator {@link ApplicantDashboardPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicantDashboardPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private ApplicantDashboardPopulator populator;

    private final static Long APPLICATION_ID_IN_PROGRESS = 1L;
    private final static Long APPLICATION_ID_IN_FINISH = 10L;
    private final static Long PROJECT_ID_IN_PROJECT = 5L;
    private final static Long APPLICATION_ID_IN_PROJECT = 15L;

    @Before
    public void setup() {
        super.setup();
        this.setupCompetition();

        List<ApplicationResource> allApplications = newApplicationResource()
                .withId(APPLICATION_ID_IN_PROGRESS,APPLICATION_ID_IN_FINISH)
                .withCompetition(competitionResource.getId(),competitionResource.getId())
                .withApplicationState(ApplicationState.OPEN,ApplicationState.REJECTED)
                .withCompetitionStatus(CompetitionStatus.OPEN, CompetitionStatus.CLOSED)
                .withCompletion(BigDecimal.valueOf(50))
                .build(2);

        when(applicationRestService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(restSuccess(allApplications));

        when(projectService.findByUser(loggedInUser.getId())).thenReturn(ServiceResult.serviceSuccess(newProjectResource()
                .withId(PROJECT_ID_IN_PROJECT)
                .withApplication(APPLICATION_ID_IN_PROJECT)
                .build(1)));

        when(applicationService.getById(APPLICATION_ID_IN_PROJECT)).thenReturn(newApplicationResource()
                .withId(APPLICATION_ID_IN_PROJECT)
                .withApplicationState(ApplicationState.SUBMITTED)
                .withCompetition(competitionResource.getId()).build());

        when(competitionService.getById(competitionResource.getId())).thenReturn(competitionResource);

        when(processRoleService.findProcessRole(loggedInUser.getId(), APPLICATION_ID_IN_PROGRESS)).thenReturn(newProcessRoleResource().withRoleName(UserRoleType.LEADAPPLICANT.getName()).build());
        when(processRoleService.findProcessRole(loggedInUser.getId(), APPLICATION_ID_IN_PROJECT)).thenReturn(newProcessRoleResource().withRoleName(UserRoleType.LEADAPPLICANT.getName()).build());
        when(processRoleService.findProcessRole(loggedInUser.getId(), APPLICATION_ID_IN_FINISH)).thenReturn(newProcessRoleResource().withRoleName(UserRoleType.APPLICANT.getName()).build());
    }

    @Test
    public void populate() {
        ApplicantDashboardViewModel viewModel = populator.populate(loggedInUser);

        assertTrue(viewModel.getApplicationsInProgressNotEmpty());
        assertTrue(viewModel.getApplicationsInFinishedNotEmpty());
        assertTrue(viewModel.getProjectsInSetupNotEmpty());

        assertEquals("Application in progress", viewModel.getApplicationInProgressText());
    }
}
