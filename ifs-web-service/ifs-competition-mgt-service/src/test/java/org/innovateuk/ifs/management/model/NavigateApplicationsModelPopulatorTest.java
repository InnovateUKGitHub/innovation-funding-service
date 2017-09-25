package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.NavigateApplicationsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NavigateApplicationsModelPopulatorTest {

    @InjectMocks
    private NavigateApplicationsModelPopulator manageApplicationsModelPopulator;

    @Mock
    private CompetitionService competitionService;

    @Test
    public void populateModel() throws Exception {

        Long competitionId = 1L;
        String competitionName = "Competition One";

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withId(competitionId)
                .withName(competitionName)
                .build();

        when(competitionService.getById(competitionId)).thenReturn(competitionResource);

        NavigateApplicationsViewModel viewModel = manageApplicationsModelPopulator.populateModel(competitionId);

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
    }
}