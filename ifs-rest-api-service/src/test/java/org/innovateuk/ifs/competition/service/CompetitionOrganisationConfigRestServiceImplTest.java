package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.junit.Test;

import static java.lang.String.format;
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
        CompetitionOrganisationConfigResource returnedResponse = new CompetitionOrganisationConfigResource(true, true);

        setupGetWithRestResultAnonymousExpectations(format("%s/find-by-competition-id/%d", COMPETITION_ORGANISATION_CONFIG_BASE_URL, 123), CompetitionOrganisationConfigResource.class, returnedResponse);
        CompetitionOrganisationConfigResource responses = service.findByCompetitionId(123).getSuccess();
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
                .build();

        setupPutWithRestResultExpectations(COMPETITION_ORGANISATION_CONFIG_BASE_URL + "/update/" + competitionId, CompetitionOrganisationConfigResource.class, resource, expected);

        CompetitionOrganisationConfigResource actual = service.update(competitionId, resource).getSuccess();
        assertEquals(expected, actual);
    }
}