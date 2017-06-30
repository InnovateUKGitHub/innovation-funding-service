package org.innovateuk.ifs.invite.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;

public class ApplicationInviteTest {
    ApplicationInvite setInvite;
    ApplicationInvite constructedInvite;
    Long inviteId;
    String name;
    String email;
    Application application;
    String hash;
    InviteOrganisation inviteOrganisation;
    InviteStatus status;

    @Before
    public void setUp() throws Exception {
        inviteId = 1L;
        name = "testname";
        email = "test@email.test";
        application = newApplication().build();
        hash = "123abc";
        inviteOrganisation = newInviteOrganisation().build();
        status = InviteStatus.OPENED;

        setInvite = newApplicationInvite()
                .withId(inviteId)
                .withName(name)
                .withEmail(email)
                .withApplication(application)
                .withHash(hash)
                .withInviteOrganisation(inviteOrganisation)
                .withStatus(status)
                .build();

        constructedInvite = new ApplicationInvite(name, email, application, inviteOrganisation, hash, status);
    }

    @Test
    public void gettingAnyAttributeAfterSettingShouldReturnCorrectValue() throws Exception {
        Assert.assertEquals(inviteId, setInvite.getId());
        Assert.assertEquals(name, setInvite.getName());
        Assert.assertEquals(email, setInvite.getEmail());
        Assert.assertEquals(application, setInvite.getTarget());
        Assert.assertEquals(hash, setInvite.getHash());
        Assert.assertEquals(inviteOrganisation, setInvite.getInviteOrganisation());
        Assert.assertEquals(status, setInvite.getStatus());
    }

    @Test
    public void constructedInviteShouldReturnCorrectAttributes() throws Exception {
        Assert.assertEquals(name, constructedInvite.getName());
        Assert.assertEquals(email, constructedInvite.getEmail());
        Assert.assertEquals(application, constructedInvite.getTarget());
        Assert.assertEquals(hash, constructedInvite.getHash());
        Assert.assertEquals(inviteOrganisation, constructedInvite.getInviteOrganisation());
        Assert.assertEquals(status, constructedInvite.getStatus());
    }
}
