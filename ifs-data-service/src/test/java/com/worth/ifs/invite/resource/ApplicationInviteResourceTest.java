package com.worth.ifs.invite.resource;

import com.worth.ifs.invite.constant.InviteStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationInviteResourceTest {
    ApplicationInviteResource setInviteResource;
    ApplicationInviteResource constructedInviteResource;

    Long inviteId;
    String name;
    String nameConfirmed;
    String email;
    Long applicationId;
    String hash;
    long inviteOrganisationId;
    InviteStatus status;

    @Before
    public void setUp() throws Exception {
        inviteId = 1L;
        name = "testname";
        nameConfirmed = "testnameConfirmed"; 
        email = "test@email.test";
        applicationId = 2L;
        hash = "123abc";
        inviteOrganisationId = 3L;
        status = InviteStatus.OPENED;

        setInviteResource = new ApplicationInviteResource();
        setInviteResource.setId(inviteId);
        setInviteResource.setName(name);
        setInviteResource.setNameConfirmed(nameConfirmed);
        setInviteResource.setEmail(email);
        setInviteResource.setApplication(applicationId);
        setInviteResource.setHash(hash);
        setInviteResource.setInviteOrganisation(inviteOrganisationId);
        setInviteResource.setStatus(status);

        constructedInviteResource = new ApplicationInviteResource(inviteId, name, email, applicationId, inviteOrganisationId, hash, status);
    }

    @Test
    public void gettingAnyAttributeAfterSettingShouldReturnCorrectValue() throws Exception {
        assertEquals(inviteId, setInviteResource.getId());
        assertEquals(name, setInviteResource.getName());
        assertEquals(nameConfirmed, setInviteResource.getNameConfirmed());
        assertEquals(email, setInviteResource.getEmail());
        assertEquals(applicationId, setInviteResource.getApplication());
        assertEquals(hash, setInviteResource.getHash());
        assertEquals((Long)inviteOrganisationId, setInviteResource.getInviteOrganisation());
        assertEquals(status, setInviteResource.getStatus());
    }

    @Test
    public void constructedInviteShouldReturnCorrectAttributes() throws Exception {
        assertEquals(inviteId, constructedInviteResource.getId());
        assertEquals(name, constructedInviteResource.getName());
        assertEquals(email, constructedInviteResource.getEmail());
        assertEquals(applicationId, constructedInviteResource.getApplication());
        assertEquals(hash, constructedInviteResource.getHash());
        assertEquals((Long)inviteOrganisationId, constructedInviteResource.getInviteOrganisation());
        assertEquals(status, constructedInviteResource.getStatus());
    }

    @Test
    public void test_getInviteOrganisationNameConfirmedSafe() throws Exception {
        ApplicationInviteResource inviteResource = new ApplicationInviteResource();

        inviteResource.setInviteOrganisationName("Unconfirmed name");
        assertEquals("Unconfirmed name", inviteResource.getInviteOrganisationNameConfirmedSafe());

        inviteResource.setInviteOrganisationNameConfirmed("Confirmed name");
        assertEquals("Confirmed name", inviteResource.getInviteOrganisationNameConfirmedSafe());
    }

}