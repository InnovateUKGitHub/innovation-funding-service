package com.worth.ifs.competition.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.competition.domain.Competition;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionListType;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class CompetitionRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionsRestServiceImpl> {

    private static final String competitionsRestURL = "/competitions";

    @Override
    protected CompetitionsRestServiceImpl registerRestServiceUnderTest() {
        CompetitionsRestServiceImpl competitionService = new CompetitionsRestServiceImpl();
        competitionService.competitionsRestURL = competitionsRestURL;
        return competitionService;
    }

    @Test
    public void test_getAll() {

        List<Competition> returnedResponse = newCompetition().build(3);

        setupGetWithRestResultExpectations(competitionsRestURL + "/findAll", competitionListType(), returnedResponse);

        List<Competition> responses = service.getAll().getSuccessObject();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void test_getCompetitionById() {

        Competition returnedResponse = newCompetition().build();

        setupGetWithRestResultExpectations(competitionsRestURL + "/findById/123", Competition.class, returnedResponse);

        Competition response = service.getCompetitionById(123L).getSuccessObject();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }
}
