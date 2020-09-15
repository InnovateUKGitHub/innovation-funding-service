package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;

public class CompetitionSetupInnovationLeadRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionSetupInnovationLeadRestServiceImpl> {

    private String COMPETITIONS_INNOVATION_LEAD_REST_URL = "/competition/setup";

    @Override
    protected CompetitionSetupInnovationLeadRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionSetupInnovationLeadRestServiceImpl();
    }

    @Test
    public void findAvailableInnovationLeadsNotAssignedToCompetition() {
        List<UserResource> returnedResponse = newUserResource().build(2);

        setupGetWithRestResultExpectations(format("%s/%d/%s", COMPETITIONS_INNOVATION_LEAD_REST_URL, 123, "innovation-leads"), userListType(), returnedResponse);

        List<UserResource> response = service.findAvailableInnovationLeadsNotAssignedToCompetition(123).getSuccess();

        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void findInnovationLeadsAssignedToCompetition() {
        List<UserResource> returnedResponse = newUserResource().build(2);

        setupGetWithRestResultExpectations(format("%s/%d/%s", COMPETITIONS_INNOVATION_LEAD_REST_URL, 123, "innovation-leads/find-added"), userListType(), returnedResponse);

        List<UserResource> response = service.findInnovationLeadsAssignedToCompetition(123).getSuccess();

        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }

    @Test
    public void addInnovationLead() {
        setupPostWithRestResultExpectations(format("%s/%d/%s/%d", COMPETITIONS_INNOVATION_LEAD_REST_URL, 123, "add-innovation-lead", 234), HttpStatus.OK);

        RestResult<Void> response = service.addInnovationLead(123, 234);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void removeInnovationLead() {
        setupPostWithRestResultExpectations(format("%s/%d/%s/%d", COMPETITIONS_INNOVATION_LEAD_REST_URL, 123, "remove-innovation-lead", 234), HttpStatus.OK);

        RestResult<Void> response = service.removeInnovationLead(123, 234);

        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
