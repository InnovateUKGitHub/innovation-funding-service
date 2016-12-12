package com.worth.ifs.invite.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.inviteOrganisationResourceListType;


import static com.worth.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.invite.resource.ApplicationInviteConstants.GET_USER_BY_HASH_MAPPING;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class InviteRestServiceImplTest extends BaseRestServiceUnitTest<InviteRestServiceImpl> {

    private static final String inviteRestURL = "/invite";
    private final String inviteHash = "asdfhoiaf9y8523rjakljnag";

    @Override
    protected InviteRestServiceImpl registerRestServiceUnderTest() {
        final InviteRestServiceImpl inviteRestService = new InviteRestServiceImpl();
        return inviteRestService;
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test_createInvitesByInviteOrganisation() throws Exception {
        final String organisationName = "OrganisationName";
        final List<ApplicationInviteResource> invites = asList(new ApplicationInviteResource());
        InviteOrganisationResource inviteOrganisationResource = new InviteOrganisationResource();
        inviteOrganisationResource.setOrganisationName(organisationName);
        inviteOrganisationResource.setInviteResources(invites);

        InviteResultsResource expected = new InviteResultsResource();
        expected.setInvitesSendSuccess(1);

        setupPostWithRestResultExpectations(inviteRestURL +  "/createApplicationInvites", InviteResultsResource.class, inviteOrganisationResource, expected, CREATED);
        RestResult<InviteResultsResource> response = service.createInvitesByInviteOrganisation(organisationName, invites);
        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccessObject());
        assertEquals(1, response.getSuccessObject().getInvitesSendSuccess());
    }

    @Test
    public void test_createInvitesByOrganisation() throws Exception {
        final Long organisationId = 1289124L;
        final List<ApplicationInviteResource> invites = newApplicationInviteResource().build(42);

        InviteOrganisationResource inviteOrganisationResource = new InviteOrganisationResource();
        inviteOrganisationResource.setOrganisation(organisationId);
        inviteOrganisationResource.setInviteResources(invites);

        InviteResultsResource expected = new InviteResultsResource();
        expected.setInvitesSendSuccess(42);

        setupPostWithRestResultExpectations(inviteRestURL +  "/createApplicationInvites", InviteResultsResource.class, inviteOrganisationResource, expected, CREATED);
        RestResult<InviteResultsResource> response = service.createInvitesByOrganisation(organisationId, invites);
        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccessObject());
        assertEquals(42, response.getSuccessObject().getInvitesSendSuccess());
    }

    @Test
    public void test_saveInvites() throws Exception {
        final List<ApplicationInviteResource> invites = newApplicationInviteResource().build(42);

        InviteResultsResource expected = new InviteResultsResource();
        expected.setInvitesSendSuccess(21);
        expected.setInvitesSendFailure(19);

        setupPostWithRestResultExpectations(inviteRestURL +  "/saveInvites", InviteResultsResource.class, invites, expected, OK);
        RestResult<InviteResultsResource> response = service.saveInvites(invites);
        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccessObject());
        assertEquals(19, response.getSuccessObject().getInvitesSendFailure());
        assertEquals(21, response.getSuccessObject().getInvitesSendSuccess());
    }


    @Test
    public void test_acceptInvite() throws Exception {
        final Long userId = 124214L;

        setupPutWithRestResultAnonymousExpectations(inviteRestURL +  String.format("/acceptInvite/%s/%s", inviteHash, userId), null, OK);

        RestResult<Void> response = service.acceptInvite(inviteHash, userId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void test_removeApplicationInvite() throws Exception {
        final Long inviteId = 20310L;

        setupDeleteWithRestResultExpectations(inviteRestURL +  String.format("/removeInvite/%s", inviteId), OK);
        RestResult<Void> response = service.removeApplicationInvite(inviteId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void test_checkExistingUser() throws Exception {
        String url = inviteRestURL + String.format("/checkExistingUser/%s", inviteHash);
        setupGetWithRestResultAnonymousExpectations(url, Boolean.class, TRUE);
        RestResult<Boolean> response = service.checkExistingUser(inviteHash);

        assertTrue(response.isSuccess());
        assertEquals(TRUE, response.getSuccessObject());
    }

    @Test
    public void test_getUser() throws Exception {
        UserResource expected = new UserResource();

        String url = inviteRestURL + String.format(GET_USER_BY_HASH_MAPPING + "%s", inviteHash);
        setupGetWithRestResultAnonymousExpectations(url, UserResource.class, expected);
        RestResult<UserResource> response = service.getUser(inviteHash);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccessObject());
    }

    @Test
    public void test_getInviteByHash() throws Exception {
        ApplicationInviteResource expected = new ApplicationInviteResource();

        String url = inviteRestURL + "/getInviteByHash/" + inviteHash;
        setupGetWithRestResultAnonymousExpectations(url, ApplicationInviteResource.class, expected);
        RestResult<ApplicationInviteResource> response = service.getInviteByHash(inviteHash);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccessObject());
    }

    @Test
    public void test_getInviteOrganisationByHash() throws Exception {
        InviteOrganisationResource expected = new InviteOrganisationResource();
        expected.setId(1234L);

        String url = inviteRestURL + "/getInviteOrganisationByHash/" + inviteHash;
        setupGetWithRestResultAnonymousExpectations(url, InviteOrganisationResource.class, expected);
        RestResult<InviteOrganisationResource> response = service.getInviteOrganisationByHash(inviteHash);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccessObject());
    }

    @Test
    public void test_getInvitesByApplication() throws Exception {
        Long applicationId = 2341L;
        List<InviteOrganisationResource> expected = newInviteOrganisationResource().build(2);
        String url = inviteRestURL + "/getInvitesByApplicationId/" + applicationId;
        setupGetWithRestResultExpectations(url, inviteOrganisationResourceListType(), expected, OK);
        RestResult<List<InviteOrganisationResource>> response = service.getInvitesByApplication(applicationId);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccessObject());
    }
}