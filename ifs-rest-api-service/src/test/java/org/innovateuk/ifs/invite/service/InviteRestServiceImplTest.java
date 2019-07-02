package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.inviteOrganisationResourceListType;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class InviteRestServiceImplTest extends BaseRestServiceUnitTest<InviteRestServiceImpl> {

    private static final String inviteRestURL = "/invite";
    private final String inviteHash = "asdfhoiaf9y8523rjakljnag";

    @Override
    protected InviteRestServiceImpl registerRestServiceUnderTest() {
        return new InviteRestServiceImpl();
    }

    @Test
    public void createInvitesByInviteOrganisation() {
        final String organisationName = "OrganisationName";
        final List<ApplicationInviteResource> invites = singletonList(new ApplicationInviteResource());
        InviteOrganisationResource inviteOrganisationResource = new InviteOrganisationResource();
        inviteOrganisationResource.setOrganisationName(organisationName);
        inviteOrganisationResource.setInviteResources(invites);

        setupPostWithRestResultExpectations(inviteRestURL +  "/create-application-invites", inviteOrganisationResource, CREATED);
        RestResult<Void> response = service.createInvitesByInviteOrganisation(organisationName, invites);
        assertTrue(response.isSuccess());

        setupPostWithRestResultVerifications(inviteRestURL +  "/create-application-invites", Void.class, inviteOrganisationResource);

    }

    @Test
    public void createInvitesByOrganisation() {
        final Long organisationId = 1289124L;
        final List<ApplicationInviteResource> invites = newApplicationInviteResource().build(42);

        InviteOrganisationResource inviteOrganisationResource = new InviteOrganisationResource();
        inviteOrganisationResource.setOrganisation(organisationId);
        inviteOrganisationResource.setInviteResources(invites);

        setupPostWithRestResultExpectations(inviteRestURL +  "/create-application-invites", inviteOrganisationResource, CREATED);
        RestResult<Void> response = service.createInvitesByOrganisation(organisationId, invites);
        assertTrue(response.isSuccess());

        setupPostWithRestResultVerifications(inviteRestURL +  "/create-application-invites", Void.class, inviteOrganisationResource);
    }

    @Test
    public void saveInvites() {
        final List<ApplicationInviteResource> invites = newApplicationInviteResource().build(42);

        setupPostWithRestResultExpectations(inviteRestURL +  "/save-invites", invites, OK);
        RestResult<Void> response = service.saveInvites(invites);
        assertTrue(response.isSuccess());

        setupPostWithRestResultVerifications(inviteRestURL +  "/save-invites", Void.class, invites);
    }

    @Test
    public void resendInvites() {
        final ApplicationInviteResource invite = newApplicationInviteResource().build();

        setupPostWithRestResultExpectations(inviteRestURL +  "/resend-invite", invite, OK);
        RestResult<Void> response = service.resendInvite(invite);
        assertTrue(response.isSuccess());

        setupPostWithRestResultVerifications(inviteRestURL +  "/resend-invite", Void.class, invite);
    }

    @Test
    public void acceptInvite() {
        final long userId = 124214L;

        setupPutWithRestResultAnonymousExpectations(inviteRestURL +  String.format("/accept-invite/%s/%s", inviteHash, userId), null, OK);

        RestResult<Void> response = service.acceptInvite(inviteHash, userId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void acceptInvite_OrganisationId() {
        final long userId = 124214L;
        final long organisationId = 23L;

        setupPutWithRestResultAnonymousExpectations(inviteRestURL +  String.format("/accept-invite/%s/%s/%s", inviteHash, userId, organisationId), null, OK);

        RestResult<Void> response = service.acceptInvite(inviteHash, userId, organisationId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void removeApplicationInvite() {
        final Long inviteId = 20310L;

        setupDeleteWithRestResultExpectations(inviteRestURL +  String.format("/remove-invite/%s", inviteId), OK);
        RestResult<Void> response = service.removeApplicationInvite(inviteId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void checkExistingUser() {
        String url = inviteRestURL + String.format("/check-existing-user/%s", inviteHash);
        setupGetWithRestResultAnonymousExpectations(url, Boolean.class, TRUE);
        RestResult<Boolean> response = service.checkExistingUser(inviteHash);

        assertTrue(response.isSuccess());
        assertEquals(TRUE, response.getSuccess());
    }

    @Test
    public void getUser() {
        UserResource expected = new UserResource();

        String url = inviteRestURL + String.format("/get-user/" + "%s", inviteHash);
        setupGetWithRestResultAnonymousExpectations(url, UserResource.class, expected);
        RestResult<UserResource> response = service.getUser(inviteHash);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccess());
    }

    @Test
    public void getInviteByHash() {
        ApplicationInviteResource expected = new ApplicationInviteResource();

        String url = inviteRestURL + "/get-invite-by-hash/" + inviteHash;
        setupGetWithRestResultAnonymousExpectations(url, ApplicationInviteResource.class, expected);
        RestResult<ApplicationInviteResource> response = service.getInviteByHash(inviteHash);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccess());
    }

    @Test
    public void getInviteOrganisationByHash() {
        InviteOrganisationResource expected = new InviteOrganisationResource();
        expected.setId(1234L);

        String url = inviteRestURL + "/get-invite-organisation-by-hash/" + inviteHash;
        setupGetWithRestResultAnonymousExpectations(url, InviteOrganisationResource.class, expected);
        RestResult<InviteOrganisationResource> response = service.getInviteOrganisationByHash(inviteHash);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccess());
    }

    @Test
    public void getInvitesByApplication() {
        Long applicationId = 2341L;
        List<InviteOrganisationResource> expected = newInviteOrganisationResource().build(2);
        String url = inviteRestURL + "/get-invites-by-application-id/" + applicationId;
        setupGetWithRestResultExpectations(url, inviteOrganisationResourceListType(), expected, OK);
        RestResult<List<InviteOrganisationResource>> response = service.getInvitesByApplication(applicationId);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccess());
    }
}
