package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.viewmodel.OrganisationalEligibilityViewModel;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrganisationalEligibilityViewModelPopulatorTest {

    @Test
    public void populateModel() {

        CompetitionResource competition = newCompetitionResource().build();
        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(true, competition, ORGANISATIONAL_ELIGIBILITY, CompetitionSetupSection.values(), true, false);

        OrganisationalEligibilityViewModel viewModel = new OrganisationalEligibilityViewModel(generalSetupViewModel);

        assertEquals(ORGANISATIONAL_ELIGIBILITY, viewModel.getGeneral().getCurrentSection());
        assertTrue(viewModel.getGeneral().isInitialComplete());
    }
}