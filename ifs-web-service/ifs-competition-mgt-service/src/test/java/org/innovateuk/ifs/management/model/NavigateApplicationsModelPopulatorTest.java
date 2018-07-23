package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.application.list.populator.NavigateApplicationsModelPopulator;
import org.innovateuk.ifs.management.application.list.viewmodel.NavigateApplicationsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NavigateApplicationsModelPopulatorTest {

    @InjectMocks
    private NavigateApplicationsModelPopulator manageApplicationsModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populateModel() throws Exception {

        Long competitionId = 1L;
        String competitionName = "Competition One";

        CompetitionResource competitionResource = CompetitionResourceBuilder.newCompetitionResource()
                .withId(competitionId)
                .withName(competitionName)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));

        NavigateApplicationsViewModel viewModel = manageApplicationsModelPopulator.populateModel(competitionId);

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
    }
}