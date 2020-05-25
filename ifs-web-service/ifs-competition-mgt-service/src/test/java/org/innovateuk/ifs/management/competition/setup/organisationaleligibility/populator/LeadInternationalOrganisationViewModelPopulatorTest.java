package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator;

import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.viewmodel.LeadInternationalOrganisationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LeadInternationalOrganisationViewModelPopulatorTest {

    @InjectMocks
    private LeadInternationalOrganisationViewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populateModel() {

        long competitionId = 100L;
        CompetitionResource competition = newCompetitionResource().withId(competitionId).build();
        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource().withInternationalLeadOrganisationAllowed(true).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        LeadInternationalOrganisationViewModel result =  populator.populateModel(competitionId, configResource);

        assertTrue(result.isLeadInternationalOrganisationsApplicable());
    }
}