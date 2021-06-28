package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerDashboardViewModelPopulatorTest {

    @InjectMocks
    private MonitoringOfficerDashboardViewModelPopulator populator;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Mock
    private MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator;

    @Test
    public void populate() {
        UserResource user = newUserResource().build();
        ProjectResource projectResource = newProjectResource()
                .withCompetition(1L)
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withProjectState(ProjectState.UNSUCCESSFUL)
                .build();

        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = Mockito.mock(MonitoringOfficerSummaryViewModel.class);

        when(monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId())).thenReturn(restSuccess(singletonList(projectResource)));
        when(monitoringOfficerSummaryViewModelPopulator.populate(anyList())).thenReturn(monitoringOfficerSummaryViewModel);

        MonitoringOfficerDashboardViewModel viewModel = populator.populate(user);
        assertEquals(1, viewModel.getProjects().size());

        assertEquals((long) projectResource.getId(), viewModel.getProjects().get(0).getProjectId());
        assertEquals(projectResource.getApplication(), viewModel.getProjects().get(0).getApplicationNumber());
        assertEquals("Competition name", viewModel.getProjects().get(0).getCompetitionTitle());
        assertEquals(String.format("/project-setup/project/%d", projectResource.getId()), viewModel.getProjects().get(0).getLinkUrl());
        assertEquals("Project name", viewModel.getProjects().get(0).getProjectTitle());
        assertTrue(viewModel.getProjects().get(0).isUnsuccessful());
        assertFalse(viewModel.getProjects().get(0).isLiveOrCompletedOffline());
        assertFalse(viewModel.getProjects().get(0).isWithdrawn());
    }

    @Test
    public void populateApplyFilterAndSorting() {
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

        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = new MonitoringOfficerSummaryViewModel(1, 1, 2, 0, 1);

        when(monitoringOfficerRestService.filterProjectsForMonitoringOfficer(user.getId(), true, true))
                .thenReturn(restSuccess(asList(projectResourceInLive, projectResourceInSetup)));
        when(monitoringOfficerSummaryViewModelPopulator.populate(user)).thenReturn(monitoringOfficerSummaryViewModel);

        MonitoringOfficerDashboardViewModel viewModel = populator.populate(user, true, true, false, true, false);
        assertEquals(2, viewModel.getProjects().size());

        assertEquals((long) projectResourceInSetup.getId(), viewModel.getProjects().get(0).getProjectId());
        assertEquals(projectResourceInSetup.getApplication(), viewModel.getProjects().get(0).getApplicationNumber());
        assertEquals("Competition name", viewModel.getProjects().get(0).getCompetitionTitle());
        assertEquals(String.format("/project-setup/project/%d", projectResourceInSetup.getId()), viewModel.getProjects().get(0).getLinkUrl());
        assertEquals("Project name", viewModel.getProjects().get(0).getProjectTitle());
        assertEquals(ProjectState.SETUP, viewModel.getProjects().get(0).getProjectState());

        assertEquals((long) projectResourceInLive.getId(), viewModel.getProjects().get(1).getProjectId());
        assertEquals(projectResourceInLive.getApplication(), viewModel.getProjects().get(1).getApplicationNumber());
        assertEquals("Competition name", viewModel.getProjects().get(1).getCompetitionTitle());
        assertEquals(String.format("/project-setup/project/%d", projectResourceInLive.getId()), viewModel.getProjects().get(1).getLinkUrl());
        assertEquals("Project name", viewModel.getProjects().get(1).getProjectTitle());
        assertEquals(ProjectState.LIVE, viewModel.getProjects().get(1).getProjectState());
    }
}
