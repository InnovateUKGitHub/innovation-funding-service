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
import org.mockito.junit.MockitoJUnitRunner;

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

        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = new MonitoringOfficerSummaryViewModel(0, 1);

        when(monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId())).thenReturn(restSuccess(singletonList(projectResource)));
        when(monitoringOfficerSummaryViewModelPopulator.populate(anyList())).thenReturn(monitoringOfficerSummaryViewModel);

        MonitoringOfficerDashboardViewModel viewModel = populator.populate(user);
        assertEquals(viewModel.getProjects().size(), 1);
        assertEquals(viewModel.getProjects().get(0).getProjectId(), (long) projectResource.getId());
        assertEquals(viewModel.getProjects().get(0).getApplicationNumber(), projectResource.getApplication());
        assertEquals(viewModel.getProjects().get(0).getCompetitionTitle(), "Competition name");
        assertEquals(viewModel.getProjects().get(0).getLinkUrl(), String.format("/project-setup/project/%d", projectResource.getId()));
        assertEquals(viewModel.getProjects().get(0).getProjectTitle(), "Project name");
        assertTrue(viewModel.getProjects().get(0).isUnsuccessful());
        assertFalse(viewModel.getProjects().get(0).isLiveOrCompletedOffline());
        assertFalse(viewModel.getProjects().get(0).isWithdrawn());
        assertEquals(0, viewModel.getMonitoringOfficerSummaryView().getInSetupProjectCount());
        assertEquals(1, viewModel.getMonitoringOfficerSummaryView().getPreviousProjectCount());
    }
}
