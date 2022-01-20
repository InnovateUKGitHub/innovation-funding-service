
package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionSetupSectionStatusMap;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_ELIGIBILITY;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.INITIAL_DETAILS;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.OK;

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

        CompetitionResource response = service.create().getSuccess();
        assertNotNull(response);
        Assert.assertEquals(competition, response);
    }

    @Test
    public void createNonIfs() {
        CompetitionResource competition = new CompetitionResource();

        setupPostWithRestResultExpectations(competitionSetupRestURL + "/non-ifs", CompetitionResource.class, null, competition, HttpStatus.CREATED);

        CompetitionResource response = service.createNonIfs().getSuccess();
        assertNotNull(response);
        Assert.assertEquals(competition, response);
    }


    @Test
    public void update() {
        CompetitionResource competition = new CompetitionResource();
        competition.setId(1L);

        setupPutWithRestResultExpectations(competitionSetupRestURL + "/" + competition.getId(), Void.class, competition, null, HttpStatus.OK);

        service.update(competition).getSuccess();
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
        long competitionId = Long.MAX_VALUE;
        String competitionCode = "1602-1";
        setupPostWithRestResultExpectations(format("%s/generate-competition-code/%s", competitionSetupRestURL, competitionId), String.class, openingDate, competitionCode, HttpStatus.OK);

        String response = service.generateCompetitionCode(competitionId, openingDate).getSuccess();
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
        final long competitionId = 4L;
        final CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        setupPutWithRestResultExpectations(format("%s/section-status/complete/%s/%s", competitionSetupRestURL, competitionId, section), Void.class, null, null );

        RestResult<Void> result = service.markSectionComplete(competitionId, section);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testMarkSectionIncomplete() {
        final long competitionId = 4L;
        final CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
        setupPutWithRestResultExpectations(format("%s/section-status/incomplete/%s/%s", competitionSetupRestURL, competitionId, section), Void.class, null, null );

        RestResult<Void> result = service.markSectionIncomplete(competitionId, section);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testMarkSubSectionComplete() {
        final long competitionId = 4L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.INITIAL_DETAILS;
        final CompetitionSetupSubsection subsection = CompetitionSetupSubsection.FINANCES;

        setupPutWithRestResultExpectations(format("%s/subsection-status/complete/%s/%s/%s", competitionSetupRestURL, competitionId, parentSection, subsection), Void.class, null, null );

        RestResult<Void> result = service.markSubSectionComplete(competitionId, parentSection, subsection);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testMarkSubSectionIncomplete() {
        final long competitionId = 4L;
        final CompetitionSetupSection parentSection = CompetitionSetupSection.INITIAL_DETAILS;
        final CompetitionSetupSubsection subsection = CompetitionSetupSubsection.FINANCES;

        setupPutWithRestResultExpectations(format("%s/subsection-status/incomplete/%s/%s/%s", competitionSetupRestURL, competitionId, parentSection, subsection), Void.class, null, null );

        RestResult<Void> result = service.markSubSectionIncomplete(competitionId, parentSection, subsection);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getSectionStatuses() {
        final long competitionId = 342L;

        Map<CompetitionSetupSection, Optional<Boolean>> expectedResult = asMap(INITIAL_DETAILS, Optional.of(TRUE), PROJECT_ELIGIBILITY, Optional.empty());
        setupGetWithRestResultExpectations(competitionSetupRestURL + "/section-status/" + competitionId, competitionSetupSectionStatusMap(), expectedResult, HttpStatus.OK);

        RestResult<Map<CompetitionSetupSection, Optional<Boolean>>> result = service.getSectionStatuses(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(expectedResult, result.getSuccess());
    }

    @Test
    public void delete() {
        long competitionId = 1L;

        setupDeleteWithRestResultExpectations(format("%s/%s", competitionSetupRestURL, competitionId));

        RestResult<Void> response = service.delete(competitionId);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void uploadCompetitionTerms() {
        long competitionId = 1L;
        String originalFilename = "filename";
        String mediaType = "media/type";
        String requestBody = "content";
        long fileSizeBytes = 1000;

        String url = format("%s/competition-terms?competitionId=%d&filename=%s", competitionSetupRestURL, competitionId, originalFilename);

        FileEntryResource expectedFileEntryResource = newFileEntryResource().build();

        setupFileUploadWithRestResultExpectations(url, FileEntryResource.class, requestBody, mediaType, fileSizeBytes, expectedFileEntryResource, OK);

        FileEntryResource result =
                service.uploadCompetitionTerms(competitionId, mediaType, fileSizeBytes, originalFilename, requestBody.getBytes()).getSuccess();

        assertEquals(expectedFileEntryResource, result);
    }

    @Test
    public void deleteCompetitionTerms() {
        long competitionId = 1L;

        String url = format("%s/competition-terms?competitionId=%d", competitionSetupRestURL, competitionId);

        setupDeleteWithRestResultExpectations(url, OK);

        RestResult<Void> result = service.deleteCompetitionTerms(competitionId);

        assertTrue(result.isSuccess());
    }
}