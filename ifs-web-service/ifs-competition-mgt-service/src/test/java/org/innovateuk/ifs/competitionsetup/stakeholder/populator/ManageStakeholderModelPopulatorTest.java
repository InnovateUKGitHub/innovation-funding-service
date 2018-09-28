package org.innovateuk.ifs.competitionsetup.stakeholder.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.stakeholder.viewmodel.ManageStakeholderViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ManageStakeholderModelPopulatorTest {

    private static final Long COMPETITION_ID = 8L;

    @InjectMocks
    private ManageStakeholderModelPopulator populator;

    @Test
    public void populateModel() {

        String competitionName = "competition1";
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withName(competitionName)
                .build();

        ManageStakeholderViewModel viewModel = populator.populateModel(competitionResource);

        assertEquals(COMPETITION_ID, viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
    }
}


