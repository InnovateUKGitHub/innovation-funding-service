package com.worth.ifs.competition.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.competition.domain.Competition;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

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

        String expectedUrl = dataServicesUrl + competitionsRestURL + "/findAll";
        Competition[] returnedResponse = newCompetition().buildArray(3, Competition.class);
        ResponseEntity<Competition[]> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Competition[].class)).thenReturn(returnedEntity);

        List<Competition> responses = service.getAll();
        assertNotNull(responses);
        assertEquals(returnedResponse[0], responses.get(0));
        assertEquals(returnedResponse[1], responses.get(1));
        assertEquals(returnedResponse[2], responses.get(2));
    }

    @Test
    public void test_getCompetitionById() {

        String expectedUrl = dataServicesUrl + competitionsRestURL + "/findById/123";
        Competition returnedResponse = newCompetition().build();
        ResponseEntity<Competition> returnedEntity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Competition.class)).thenReturn(returnedEntity);

        Competition response = service.getCompetitionById(123L);
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }
}
