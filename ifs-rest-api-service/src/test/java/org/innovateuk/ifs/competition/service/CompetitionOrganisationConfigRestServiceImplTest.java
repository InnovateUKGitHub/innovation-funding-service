package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.junit.Assert.assertEquals;

public class CompetitionOrganisationConfigRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionOrganisationConfigRestServiceImpl> {

    private String url = "/competition-organisation-config";


    @Override
    protected CompetitionOrganisationConfigRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionOrganisationConfigRestServiceImpl();
    }

    @Test
    public void findByCompetitionId() {
        long competitionId = 100L;
        CompetitionOrganisationConfigResource resource = newCompetitionOrganisationConfigResource()
                .build();

        setupGetWithRestResultExpectations(url + "/find-by-competition-id/" + competitionId, CompetitionOrganisationConfigResource.class, resource);

        CompetitionOrganisationConfigResource actual = service.findByCompetitionId(competitionId).getSuccess();
        assertEquals(resource, actual);
    }

    @Test
    public void updateOrganisationalEligibility() {
        long competitionId = 100L;
        CompetitionOrganisationConfigResource resource = newCompetitionOrganisationConfigResource()
                .build();
        CompetitionOrganisationConfigResource expected = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(true)
                .build();

        setupPutWithRestResultExpectations(url + "/update/" + competitionId, CompetitionOrganisationConfigResource.class, resource, expected);

        CompetitionOrganisationConfigResource actual = service.update(competitionId, resource).getSuccess();
        assertEquals(expected, actual);
    }
}