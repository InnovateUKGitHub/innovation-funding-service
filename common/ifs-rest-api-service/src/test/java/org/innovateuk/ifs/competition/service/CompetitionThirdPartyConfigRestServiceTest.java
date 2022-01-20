package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

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
    public void create() {
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .withTermsAndConditionsLabel("terms and conditions")
                .withTermsAndConditionsGuidance("terms and conditions guidance")
                .withProjectCostGuidanceUrl("project cost guidance url")
                .withCompetitionId(competitionId)
                .build();

        setupPostWithRestResultExpectations(competitionThirdPartyConfigUrl, CompetitionThirdPartyConfigResource.class,
                competitionThirdPartyConfigResource, competitionThirdPartyConfigResource, HttpStatus.OK);

        RestResult<CompetitionThirdPartyConfigResource> response = service.create(competitionThirdPartyConfigResource);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(competitionThirdPartyConfigResource, response.getSuccess());
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

    @Test
    public void update() {
        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = newCompetitionThirdPartyConfigResource()
                .withId(1L)
                .withTermsAndConditionsLabel("updated terms and conditions")
                .withTermsAndConditionsGuidance("updated terms and conditions guidance")
                .withProjectCostGuidanceUrl("updated project cost guidance url")
                .build();

        setupPutWithRestResultExpectations(competitionThirdPartyConfigUrl + "/" + competitionId,
                competitionThirdPartyConfigResource, HttpStatus.OK);

        RestResult<Void> response = service.update(competitionId, competitionThirdPartyConfigResource);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
