package com.worth.ifs.invite.resource;

import com.worth.ifs.invite.constant.InviteStatusConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InviteResourceTest {
    InviteResource setInviteResource;
    InviteResource constructedInviteResource;

    Long inviteId;
    String name;
    String nameConfirmed;
    String email;
    Long applicationId;
    String hash;
    long inviteOrganisationId;
    InviteStatusConstants status;

    @Before
    public void setUp() throws Exception {
        inviteId = 1L;
        name = "testname";
        nameConfirmed = "testnameConfirmed"; 
        email = "test@email.test";
        applicationId = 2L;
        hash = "123abc";
        inviteOrganisationId = 3L;
        status = InviteStatusConstants.ACCEPTED;

        setInviteResource = new InviteResource();
        setInviteResource.setId(inviteId);
        setInviteResource.setName(name);
        setInviteResource.setNameConfirmed(nameConfirmed);
        setInviteResource.setEmail(email);
        setInviteResource.setApplication(applicationId);
        setInviteResource.setHash(hash);
        setInviteResource.setInviteOrganisation(inviteOrganisationId);
        setInviteResource.setStatus(status);

        constructedInviteResource = new InviteResource(inviteId, name, email, applicationId, inviteOrganisationId, hash, status);
    }

    @Test
    public void gettingAnyAttributeAfterSettingShouldReturnCorrectValue() throws Exception {
        Assert.assertEquals(inviteId, setInviteResource.getId());
        Assert.assertEquals(name, setInviteResource.getName());
        Assert.assertEquals(nameConfirmed, setInviteResource.getNameConfirmed());
        Assert.assertEquals(email, setInviteResource.getEmail());
        Assert.assertEquals(applicationId, setInviteResource.getApplication());
        Assert.assertEquals(hash, setInviteResource.getHash());
        Assert.assertEquals((Long)inviteOrganisationId, setInviteResource.getInviteOrganisation());
        Assert.assertEquals(status, setInviteResource.getStatus());
    }

    @Test
    public void constructedInviteShouldReturnCorrectAttributes() throws Exception {
        Assert.assertEquals(inviteId, constructedInviteResource.getId());
        Assert.assertEquals(name, constructedInviteResource.getName());
        Assert.assertEquals(email, constructedInviteResource.getEmail());
        Assert.assertEquals(applicationId, constructedInviteResource.getApplication());
        Assert.assertEquals(hash, constructedInviteResource.getHash());
        Assert.assertEquals((Long)inviteOrganisationId, constructedInviteResource.getInviteOrganisation());
        Assert.assertEquals(status, constructedInviteResource.getStatus());
    }

}