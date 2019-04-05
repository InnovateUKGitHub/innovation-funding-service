package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerDashboardViewModelPopulatorTest {

    @InjectMocks
    private MonitoringOfficerDashboardViewModelPopulator populator;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populate() {
        UserResource user = newUserResource().build();
        ProjectResource projectResource = newProjectResource()
                .withCompetition(1L)
                .withApplication(2L)
                .withName("Project name")
                .build();

        when(monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId())).thenReturn(restSuccess(singletonList(projectResource)));
        when(competitionRestService.getCompetitionById(1L)).thenReturn(restSuccess(newCompetitionResource()
                .withName("Competition name")
                .build()));

        MonitoringOfficerDashboardViewModel viewModel = populator.populate(user);

        assertEquals(viewModel.getProjects().size(), 1);
        assertEquals(viewModel.getProjects().get(0).getProjectId(), (long) projectResource.getId());
        assertEquals(viewModel.getProjects().get(0).getApplicationNumber(), (long) projectResource.getApplication());
        assertEquals(viewModel.getProjects().get(0).getCompetitionTitle(), "Competition name");
        assertEquals(viewModel.getProjects().get(0).getLinkUrl(), String.format("/project-setup/project/%d", projectResource.getId()));
        assertEquals(viewModel.getProjects().get(0).getProjectTitle(), "Project name");
    }
}
