package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompetitionThirdPartyConfigRestServiceTest extends BaseRestServiceUnitTest<CompetitionThirdPartyConfigRestServiceImpl> {

    private String competitionThirdPartyConfigUrl = "/competition-third-party-config";
    private static final Long competitionId = 1L;

    @Override
    protected CompetitionThirdPartyConfigRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionThirdPartyConfigRestServiceImpl();
    }

    @Test
    public void findOneByCompetitionId() {
        CompetitionThirdPartyConfigResource returnedResponse = newCompetitionThirdPartyConfigResource().build();

        setupGetWithRestResultExpectations(competitionThirdPartyConfigUrl + "/" + competitionId,
                CompetitionThirdPartyConfigResource.class, returnedResponse);

        CompetitionThirdPartyConfigResource response = service.findOneByCompetitionId(competitionId).getSuccess();

        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }
}
