package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.LeadInternationalOrganisationForm;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class LeadInternationalOrganisationFormPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private LeadInternationalOrganisationFormPopulator service;

    @Mock
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Test
    public void populateForm() {
        long competitionId=100L;
        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).build();
        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource().withInternationalLeadOrganisationAllowed(true).build();
        LeadInternationalOrganisationForm leadInternationalOrganisationForm = new LeadInternationalOrganisationForm();
        leadInternationalOrganisationForm.setLeadInternationalOrganisationsApplicable(configResource.getInternationalLeadOrganisationAllowed());

        when(competitionOrganisationConfigRestService.findByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(configResource));

        LeadInternationalOrganisationForm result = service.populateForm(competitionResource);

        assertTrue(result.getLeadInternationalOrganisationsApplicable());
    }


}