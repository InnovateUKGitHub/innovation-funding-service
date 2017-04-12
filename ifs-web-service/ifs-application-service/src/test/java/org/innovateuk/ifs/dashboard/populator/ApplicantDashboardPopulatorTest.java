package org.innovateuk.ifs.dashboard.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.dashboard.viewmodel.ApplicantDashboardViewModel;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
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
    private final static Long COMPETITION_ID = 123L;

    @Before
    public void setup() {
        super.setup();
        this.setupCompetition();

        when(applicationService.getProgress(loggedInUser.getId())).thenReturn(asMap(APPLICATION_ID_IN_PROGRESS, 50));

        when(applicationService.getInProgress(loggedInUser.getId())).thenReturn(newApplicationResource()
                .withId(APPLICATION_ID_IN_PROGRESS)
                .withCompetition(COMPETITION_ID)
                .withApplicationStatus(ApplicationStatus.OPEN)
                .build(1));
        when(applicationService.getFinished(loggedInUser.getId())).thenReturn(newApplicationResource()
                .withId(APPLICATION_ID_IN_FINISH)
                .withApplicationStatus(ApplicationStatus.REJECTED)
                .withCompetition(COMPETITION_ID)
                .build(1));

        when(projectService.findByUser(loggedInUser.getId())).thenReturn(ServiceResult.serviceSuccess(newProjectResource()
                .withId(PROJECT_ID_IN_PROJECT)
                .withApplication(APPLICATION_ID_IN_PROJECT)
                .build(1)));

        when(applicationService.getById(APPLICATION_ID_IN_PROJECT)).thenReturn(newApplicationResource()
                .withId(APPLICATION_ID_IN_PROJECT)
                .withApplicationStatus(ApplicationStatus.SUBMITTED)
                .withCompetition(COMPETITION_ID).build());

        when(competitionService.getById(COMPETITION_ID)).thenReturn(competitionResource);

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
