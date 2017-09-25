package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.UnsuccessfulApplicationsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UnsuccessfulApplicationsModelPopulatorTest {

    @InjectMocks
    private UnsuccessfulApplicationsModelPopulator unsuccessfulApplicationsModelPopulator;

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

        List<ApplicationResource> unsuccessfulApplications = ApplicationResourceBuilder.newApplicationResource().build(2);

        when(competitionService.getById(competitionId)).thenReturn(competitionResource);
        when(competitionService.findUnsuccessfulApplications(competitionId)).thenReturn(unsuccessfulApplications);

        UnsuccessfulApplicationsViewModel viewModel = unsuccessfulApplicationsModelPopulator.populateModel(competitionId);

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
        assertEquals(unsuccessfulApplications, viewModel.getUnsuccessfulApplications());
        assertEquals(unsuccessfulApplications.size(), viewModel.getUnsuccessfulApplicationsSize());
    }
}