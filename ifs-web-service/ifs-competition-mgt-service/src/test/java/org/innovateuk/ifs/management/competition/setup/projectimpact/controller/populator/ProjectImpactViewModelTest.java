package org.innovateuk.ifs.management.competition.setup.projectimpact.controller.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.projectimpact.viewmodel.ProjectImpactViewModel;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_IMPACT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProjectImpactViewModelTest {

    @Test
    public void populateModel() {

        CompetitionResource competition = newCompetitionResource().build();
        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(true, false, competition, PROJECT_IMPACT, CompetitionSetupSection.values(), true, false, true,true);


        ProjectImpactViewModel viewModel = new ProjectImpactViewModel(generalSetupViewModel);

        assertEquals(PROJECT_IMPACT, viewModel.getGeneral().getCurrentSection());
        assertTrue(viewModel.getGeneral().isInitialComplete());
    }
}