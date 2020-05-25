package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator;

import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.viewmodel.LeadInternationalOrganisationViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class LeadInternationalOrganisationViewModelPopulatorTest {

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populateModel() {

        CompetitionResource competition = newCompetitionResource().withId(100L).build();
        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource().withInternationalLeadOrganisationAllowed(true).build();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        LeadInternationalOrganisationViewModel viewModel = new LeadInternationalOrganisationViewModel(competition, configResource.getInternationalLeadOrganisationAllowed());

        assertTrue(viewModel.isLeadInternationalOrganisationsApplicable());
    }
}