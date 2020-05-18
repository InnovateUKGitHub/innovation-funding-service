package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

public class CompetitionRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionRestServiceImpl> {

    private static final String COMPETITIONS_REST_URL = "/competition";

    @Override
    protected CompetitionRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionRestServiceImpl();
    }

    @Test
    public void getAll() {
        List<CompetitionResource> returnedResponse = newCompetitionResource().build(3);
        setupGetWithRestResultExpectations(format("%s/find-all", COMPETITIONS_REST_URL), competitionResourceListType(), returnedResponse);
        List<CompetitionResource> responses = service.getAll().getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void getCompetitionById() {
        CompetitionResource returnedResponse = new CompetitionResource();

        setupGetWithRestResultExpectations(format("%s/%d", COMPETITIONS_REST_URL, 123), CompetitionResource.class, returnedResponse);

        CompetitionResource response = service.getCompetitionById(123).getSuccess();
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void findInnovationLeads() {
        List<UserResource> returnedResponse = newUserResource().build(2);

        setupGetWithRestResultExpectations(format("%s/%d/%s", COMPETITIONS_REST_URL, 123, "innovation-leads"), userListType(), returnedResponse);

        List<UserResource> response = service.findInnovationLeads(123).getSuccess();

        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void addInnovationLead() {
        setupPostWithRestResultExpectations(format("%s/%d/%s/%d", COMPETITIONS_REST_URL, 123, "add-innovation-lead", 234), HttpStatus.OK);

        RestResult<Void> response = service.addInnovationLead(123, 234);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void removeInnovationLead() {
        setupPostWithRestResultExpectations(format("%s/%d/%s/%d", COMPETITIONS_REST_URL, 123, "remove-innovation-lead", 234), HttpStatus.OK);

        RestResult<Void> response = service.removeInnovationLead(123, 234);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getCompetitionTypes() {
        List<CompetitionTypeResource> returnedResponse = asList(new CompetitionTypeResource(), new CompetitionTypeResource());

        setupGetWithRestResultExpectations("/competition-type/find-all", competitionTypeResourceListType(), returnedResponse);

        List<CompetitionTypeResource> response = service.getCompetitionTypes().getSuccess();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(returnedResponse, response);
    }

    @Test
    public void updateTermsAndConditionsForCompetition() {
        setupPutWithRestResultExpectations(format("%s/%d/%s/%d", COMPETITIONS_REST_URL, 123, "update-terms-and-conditions", 234), HttpStatus.OK);

        RestResult<Void> response = service.updateTermsAndConditionsForCompetition(123, 234);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void downloadTerms() {
        long competitionId = 7;
        ByteArrayResource expectedResource = new ByteArrayResource("content".getBytes());
        setupGetWithRestResultExpectations(format("%s/%d/terms-and-conditions", COMPETITIONS_REST_URL, competitionId), ByteArrayResource.class, expectedResource, HttpStatus.OK);

        ByteArrayResource actualResource = service.downloadTerms(competitionId).getSuccess();

        assertEquals(expectedResource, actualResource);
    }
}