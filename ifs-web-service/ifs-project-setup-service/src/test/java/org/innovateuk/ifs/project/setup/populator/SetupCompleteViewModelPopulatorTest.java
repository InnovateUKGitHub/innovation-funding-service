package org.innovateuk.ifs.project.setup.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.setup.viewmodel.SetupCompleteViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SetupCompleteViewModelPopulatorTest {

    @InjectMocks
    private SetupCompleteViewModelPopulator service;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private CompetitionRestService competitionRestServiceMock;

    @Test
    public void populate() {

        long projectId = 1L;
        long competitionId = 2L;

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withName("Project Name")
                .withProjectState(LIVE)
                .withCompetition(competitionId)
                .build();

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(competitionId)
                .withName("Comp Name")
                .build();

        when(projectServiceMock.getById(projectId)).thenReturn(projectResource);
        when(competitionRestServiceMock.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));

        SetupCompleteViewModel viewModel = service.populate(projectId);

        assertEquals(viewModel.getCompetitionId(), competitionId);
        assertEquals(viewModel.getProjectId(), projectId);

        verify(projectServiceMock).getById(projectId);
        verify(competitionRestServiceMock).getCompetitionById(competitionId);
    }
}
