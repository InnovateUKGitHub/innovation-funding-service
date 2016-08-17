package com.worth.ifs.competition.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.competition.resource.CompetitionCountResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSearchResult;
import com.worth.ifs.competition.resource.CompetitionTypeResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionResourceListType;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.competitionTypeResourceListType;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static java.util.Arrays.asList;
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

    @Test
    public void test_getCompetitionTypes() {
        List<CompetitionTypeResource> returnedResponse = asList(new CompetitionTypeResource(), new CompetitionTypeResource());

        setupGetWithRestResultExpectations("/competition-type/findAll", competitionTypeResourceListType(), returnedResponse);

        List<CompetitionTypeResource> response = service.getCompetitionTypes().getSuccessObject();
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_create() {
        CompetitionResource competition = new CompetitionResource();

        setupPostWithRestResultExpectations(competitionsRestURL + "", CompetitionResource.class, null, competition, HttpStatus.CREATED);

        CompetitionResource response = service.create().getSuccessObject();
        assertNotNull(response);
        assertEquals(competition, response);
    }


    @Test
    public void test_update() {
        CompetitionResource competition = new CompetitionResource();
        competition.setId(1L);

        setupPutWithRestResultExpectations(competitionsRestURL + "/" + competition.getId(), Void.class, competition, null, HttpStatus.OK);

        service.update(competition).getSuccessObject();
    }

    @Test
    public void test_generateCompetitionCode() {
        LocalDateTime openingDate = LocalDateTime.of(2016, 2, 1, 0, 0);
        Long competitionId = Long.MAX_VALUE;
        String competitionCode = "1602-1";
        setupPostWithRestResultExpectations(String.format("%s/generateCompetitionCode/%s", competitionsRestURL, competitionId), String.class, openingDate, competitionCode, HttpStatus.OK);

        String response = service.generateCompetitionCode(competitionId, openingDate).getSuccessObject();
        assertNotNull(response);
        assertEquals(competitionCode, response);
    }

    @Test
    public void test_findLiveCompetitions() {
        List<CompetitionResource> returnedResponse = newCompetitionResource().build(3);

        setupGetWithRestResultExpectations(competitionsRestURL + "/live", competitionResourceListType(), returnedResponse);

        List<CompetitionResource> responses = service.findLiveCompetitions().getSuccessObject();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void test_findProjectSetupCompetitions() {
        List<CompetitionResource> returnedResponse = newCompetitionResource().build(3);

        setupGetWithRestResultExpectations(competitionsRestURL + "/projectSetup", competitionResourceListType(), returnedResponse);

        List<CompetitionResource> responses = service.findProjectSetupCompetitions().getSuccessObject();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void test_findUpcomingCompetitions() {
        List<CompetitionResource> returnedResponse = newCompetitionResource().build(3);

        setupGetWithRestResultExpectations(competitionsRestURL + "/upcoming", competitionResourceListType(), returnedResponse);

        List<CompetitionResource> responses = service.findUpcomingCompetitions().getSuccessObject();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void test_countCompetitions() {
        CompetitionCountResource returnedResponse = new CompetitionCountResource();

        setupGetWithRestResultExpectations(competitionsRestURL + "/count", CompetitionCountResource.class, returnedResponse);

        CompetitionCountResource responses = service.countCompetitions().getSuccessObject();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void test_searchCompetitions() {
        CompetitionSearchResult returnedResponse = new CompetitionSearchResult();
        String searchQuery = "SearchQuery";
        int page = 1;
        int size = 20;

        setupGetWithRestResultExpectations(competitionsRestURL + "/search/" + page + "/" + size + "?searchQuery=" + searchQuery, CompetitionSearchResult.class, returnedResponse);

        CompetitionSearchResult responses = service.searchCompetitions(searchQuery, page, size).getSuccessObject();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }
}
