package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.OrganisationalEligibilityForm;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class OrganisationalEligibilityFormPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private OrganisationalEligibilityFormPopulator service;

    @Mock
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Test
    public void sectionToFill() {
        assertEquals(service.sectionToFill(), ORGANISATIONAL_ELIGIBILITY);
    }

    @Test
    public void populateForm() {
        long competitionId=100L;
        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).build();
        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource().withInternationalOrganisationsAllowed(true).build();

        when(competitionOrganisationConfigRestService.findByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(configResource));

        OrganisationalEligibilityForm result = (OrganisationalEligibilityForm) service.populateForm(competitionResource);

        assertTrue(result.getInternationalOrganisationsApplicable());
    }


}