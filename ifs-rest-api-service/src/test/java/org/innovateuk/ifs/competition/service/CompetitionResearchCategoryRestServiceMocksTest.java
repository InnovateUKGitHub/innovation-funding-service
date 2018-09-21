package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionResearchCategoryLinkResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionResearchCategoryLinkList;
import static org.innovateuk.ifs.competition.builder.CompetitionResearchCategoryLinkResourceBuilder.newCompetitionResearchCategoryLinkResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompetitionResearchCategoryRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionResearchCategoryRestServiceImpl> {

    private static String url = "/competition-research-category";

    @Test
    public void findByCompetition() {
        Long competitionId = 1L;
        List<CompetitionResearchCategoryLinkResource> expectedResponse = newCompetitionResearchCategoryLinkResource().build(4);

        setupGetWithRestResultExpectations(url + "/" + competitionId, competitionResearchCategoryLinkList(), expectedResponse, HttpStatus.OK);
        List<CompetitionResearchCategoryLinkResource> response = service.findByCompetition(competitionId).getSuccess();

        assertNotNull(response);
        assertEquals(response, expectedResponse);
    }

    @Override
    protected CompetitionResearchCategoryRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionResearchCategoryRestServiceImpl();
    }
}
