package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerSummaryViewModelPopulatorTest {

    @InjectMocks
    private MonitoringOfficerSummaryViewModelPopulator populator;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Test
    public void populateByProjects() {
        ProjectResource projectResourceInSetup = newProjectResource()
                .withCompetition(1L)
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withProjectState(ProjectState.SETUP)
                .build();
        ProjectResource projectResourceInLive = newProjectResource()
                .withCompetition(1L)
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withProjectState(ProjectState.LIVE)
                .build();

        MonitoringOfficerSummaryViewModel viewModel = populator.populate(asList(projectResourceInSetup, projectResourceInLive));

        assertEquals(1, viewModel.getInSetupProjectCount());
        assertEquals(1, viewModel.getPreviousProjectCount());
    }

    @Test
    public void populateByUser() {
        UserResource user = newUserResource().build();
        ProjectResource projectResourceInSetup = newProjectResource()
                .withCompetition(1L)
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withProjectState(ProjectState.SETUP)
                .build();
        ProjectResource projectResourceInLive = newProjectResource()
                .withCompetition(1L)
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withProjectState(ProjectState.LIVE)
                .build();

        when(monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()))
                .thenReturn(restSuccess(asList(projectResourceInSetup, projectResourceInLive)));

        MonitoringOfficerSummaryViewModel viewModel = populator.populate(user);

        assertEquals(1, viewModel.getInSetupProjectCount());
        assertEquals(1, viewModel.getPreviousProjectCount());
    }
}
