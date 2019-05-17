
package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;

public class CompetitionRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionRestServiceImpl> {

    private static final String competitionsRestURL = "/competition";

    @Override
    protected CompetitionRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionRestServiceImpl();
    }

    @Test
    public void getAll() {

        List<CompetitionResource> returnedResponse = newCompetitionResource().build(3);
        setupGetWithRestResultExpectations(competitionsRestURL + "/find-all", competitionResourceListType(), returnedResponse);
        List<CompetitionResource> responses = service.getAll().getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void getCompetitionById() {

        CompetitionResource returnedResponse = new CompetitionResource();

        setupGetWithRestResultExpectations(competitionsRestURL + "/123", CompetitionResource.class, returnedResponse);

        CompetitionResource response = service.getCompetitionById(123L).getSuccess();
        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void findInnovationLeads() {

        List<UserResource> returnedResponse = asList(new UserResource(), new UserResource());

        setupGetWithRestResultExpectations(competitionsRestURL + "/123" + "/innovation-leads", userListType(), returnedResponse);

        List<UserResource> response = service.findInnovationLeads(123L).getSuccess();
        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void addInnovationLead() {

        setupPostWithRestResultExpectations(competitionsRestURL + "/123" + "/add-innovation-lead" + "/234", HttpStatus.OK);

        RestResult<Void> response = service.addInnovationLead(123L, 234L);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void removeInnovationLead() {

        setupPostWithRestResultExpectations(competitionsRestURL + "/123" + "/remove-innovation-lead" + "/234", HttpStatus.OK);

        RestResult<Void> response = service.removeInnovationLead(123L, 234L);
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
        setupPutWithRestResultExpectations(competitionsRestURL + "/123" + "/update-terms-and-conditions" + "/234", HttpStatus.OK);

        RestResult<Void> response = service.updateTermsAndConditionsForCompetition(123L, 234L);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
