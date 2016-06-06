package com.worth.ifs.competition.service;

import java.util.List;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.competition.resource.CompetitionResource;

import org.junit.Test;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionResourceListType;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class CompetitionRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionsRestServiceImpl> {

    private static final String competitionsRestURL = "/competition";

    @Override
    protected CompetitionsRestServiceImpl registerRestServiceUnderTest() {
        CompetitionsRestServiceImpl competitionService = new CompetitionsRestServiceImpl();
        return competitionService;
    }

    @Test
    public void test_getAll() {

        List<CompetitionResource> returnedResponse = newCompetitionResource().build(3);

        setupGetWithRestResultExpectations(competitionsRestURL + "/findAll", competitionResourceListType(), returnedResponse);

        List<CompetitionResource> responses = service.getAll().getSuccessObject();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void test_getCompetitionById() {

        CompetitionResource returnedResponse = newCompetitionResource().build();

        setupGetWithRestResultExpectations(competitionsRestURL + "/123", CompetitionResource.class, returnedResponse);

        CompetitionResource response = service.getCompetitionById(123L).getSuccessObject();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }
}
