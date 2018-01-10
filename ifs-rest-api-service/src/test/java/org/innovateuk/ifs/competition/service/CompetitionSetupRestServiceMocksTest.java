
package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionSetupSectionStatusMap;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ELIGIBILITY;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.INITIAL_DETAILS;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;

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
        setupPostWithRestResultExpectations(String.format("%s/generate-competition-code/%s", competitionSetupRestURL, competitionId), String.class, openingDate, competitionCode, HttpStatus.OK);

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
    public void testMarkSectionComplete() {
        final Long competitionId = 4L;
        final CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        setupPutWithRestResultExpectations(String.format("%s/section-status/complete/%s/%s", competitionSetupRestURL, competitionId, section), Void.class, null, null );

        RestResult<Void> result = service.markSectionComplete(competitionId, section);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testMarkSectionIncomplete() {
        final Long competitionId = 4L;
        final CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        setupPutWithRestResultExpectations(String.format("%s/section-status/incomplete/%s/%s", competitionSetupRestURL, competitionId, section), Void.class, null, null );

        RestResult<Void> result = service.markSectionIncomplete(competitionId, section);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testMarkSubSectionComplete() {
        final Long competitionId = 4L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.INITIAL_DETAILS;
        final CompetitionSetupSubsection subsection = CompetitionSetupSubsection.FINANCES;

        setupPutWithRestResultExpectations(String.format("%s/subsection-status/complete/%s/%s/%s", competitionSetupRestURL, competitionId, parentSection, subsection), Void.class, null, null );

        RestResult<Void> result = service.markSubSectionComplete(competitionId, parentSection, subsection);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testMarkSubSectionIncomplete() {
        final Long competitionId = 4L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.INITIAL_DETAILS;
        final CompetitionSetupSubsection subsection = CompetitionSetupSubsection.FINANCES;

        setupPutWithRestResultExpectations(String.format("%s/subsection-status/incomplete/%s/%s/%s", competitionSetupRestURL, competitionId, parentSection, subsection), Void.class, null, null );

        RestResult<Void> result = service.markSubSectionIncomplete(competitionId, parentSection, subsection);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getSectionStatuses() {
        final Long competitionId = 342L;

        Map<CompetitionSetupSection, Optional<Boolean>> expectedResult = asMap(INITIAL_DETAILS, Optional.of(TRUE), ELIGIBILITY, Optional.empty());
        setupGetWithRestResultExpectations(competitionSetupRestURL + "/section-status/" + competitionId, competitionSetupSectionStatusMap(), expectedResult, HttpStatus.OK);

        RestResult<Map<CompetitionSetupSection, Optional<Boolean>>> result = service.getSectionStatuses(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(expectedResult, result.getSuccessObject());
    }
}
