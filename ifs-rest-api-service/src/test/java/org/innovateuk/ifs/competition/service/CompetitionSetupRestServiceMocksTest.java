
package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionSetupSectionStatusMap;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ELIGIBILITY;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.INITIAL_DETAILS;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;

/**
 *
 */
public class CompetitionSetupRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionSetupRestServiceImpl> {

    private static final String competitionSetupRestURL = "/competition/setup";

    @Override
    protected CompetitionSetupRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionSetupRestServiceImpl();
    }

    @Test
    public void create() {
        CompetitionResource competition = new CompetitionResource();

        setupPostWithRestResultExpectations(competitionSetupRestURL + "", CompetitionResource.class, null, competition, HttpStatus.CREATED);

        CompetitionResource response = service.create().getSuccessObject();
        assertNotNull(response);
        Assert.assertEquals(competition, response);
    }

    @Test
    public void createNonIfs() {
        CompetitionResource competition = new CompetitionResource();

        setupPostWithRestResultExpectations(competitionSetupRestURL + "/non-ifs", CompetitionResource.class, null, competition, HttpStatus.CREATED);

        CompetitionResource response = service.createNonIfs().getSuccessObject();
        assertNotNull(response);
        Assert.assertEquals(competition, response);
    }


    @Test
    public void update() {
        CompetitionResource competition = new CompetitionResource();
        competition.setId(1L);

        setupPutWithRestResultExpectations(competitionSetupRestURL + "/" + competition.getId(), Void.class, competition, null, HttpStatus.OK);

        service.update(competition).getSuccessObject();
    }

    @Test
    public void updateCompetitionInitialDetails() {

        CompetitionResource competition = new CompetitionResource();
        competition.setId(1L);

        setupPutWithRestResultExpectations(competitionSetupRestURL + "/" + competition.getId() + "/update-competition-initial-details", Void.class, competition, null, HttpStatus.OK);

        RestResult<Void> response = service.updateCompetitionInitialDetails(competition);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void generateCompetitionCode() {
        ZonedDateTime openingDate = ZonedDateTime.of(2016, 2, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        Long competitionId = Long.MAX_VALUE;
        String competitionCode = "1602-1";
        setupPostWithRestResultExpectations(String.format("%s/generateCompetitionCode/%s", competitionSetupRestURL, competitionId), String.class, openingDate, competitionCode, HttpStatus.OK);

        String response = service.generateCompetitionCode(competitionId, openingDate).getSuccessObject();
        assertNotNull(response);
        assertEquals(competitionCode, response);
    }

    @Test
    public void markAsSetup() {
        long competitionId = 1L;
        setupPostWithRestResultExpectations(competitionSetupRestURL + "/" + competitionId + "/mark-as-setup", HttpStatus.OK);

        RestResult<Void> result = service.markAsSetup(competitionId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void returnToSetup() {
        long competitionId = 1L;
        setupPostWithRestResultExpectations(competitionSetupRestURL + "/" + competitionId + "/return-to-setup", HttpStatus.OK);

        RestResult<Void> result = service.returnToSetup(competitionId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getSectionStatuses() {
        final Long competitionId = 342L;

        Map<CompetitionSetupSection, Boolean> expectedResult = asMap(INITIAL_DETAILS, TRUE, ELIGIBILITY, FALSE);
        setupGetWithRestResultExpectations(competitionSetupRestURL + "/sectionStatus/" + competitionId, competitionSetupSectionStatusMap(), expectedResult, HttpStatus.OK);

        RestResult<Map<CompetitionSetupSection, Boolean>> result = service.getSectionStatuses(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(expectedResult, result.getSuccessObject());
    }
}
