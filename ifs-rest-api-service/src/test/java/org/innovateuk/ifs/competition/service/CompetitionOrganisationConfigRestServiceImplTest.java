package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompetitionOrganisationConfigRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionOrganisationConfigRestServiceImpl> {

    private static final String COMPETITION_ORGANISATION_CONFIG_BASE_URL = "/competition-organisation-config";

    @Override
    protected CompetitionOrganisationConfigRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionOrganisationConfigRestServiceImpl();
    }

    @Test
    public void findOrganisationConfigByCompetitionId() {
        long competitionId = 123L;

        CompetitionOrganisationConfigResource returnedResponse = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(true)
                .withInternationalLeadOrganisationAllowed(true)
                .build();

        setupGetWithRestResultExpectations(COMPETITION_ORGANISATION_CONFIG_BASE_URL + "/find-by-competition-id/" + competitionId, CompetitionOrganisationConfigResource.class, returnedResponse);
        CompetitionOrganisationConfigResource responses = service.findByCompetitionId(competitionId).getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void updateOrganisationalEligibility() {
        long competitionId = 100L;
        CompetitionOrganisationConfigResource resource = newCompetitionOrganisationConfigResource()
                .build();
        CompetitionOrganisationConfigResource expected = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(true)
                .withInternationalLeadOrganisationAllowed(true)
                .build();

        setupPutWithRestResultExpectations(COMPETITION_ORGANISATION_CONFIG_BASE_URL + "/update/" + competitionId, CompetitionOrganisationConfigResource.class, resource, expected);

        CompetitionOrganisationConfigResource actual = service.update(competitionId, resource).getSuccess();
        assertEquals(expected, actual);
    }
}